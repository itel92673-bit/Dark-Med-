#include <jni.h>
#include <atomic>
#include <cstddef>
#include <mutex>
#include <thread>

extern "C" {
#include "hev-main.h"
#include "hev-task.h"
#include "hev-task-io.h"
#include "hev-task-io-socket.h"
}

namespace {
std::mutex g_mutex;
std::mutex g_protector_mutex;
std::thread g_worker;
std::atomic<bool> g_running{false};
std::atomic<int> g_last_result{0};
JavaVM* g_vm = nullptr;
jobject g_protector = nullptr;
jmethodID g_protect_method = nullptr;

void clear_protector(JNIEnv* env) {
    std::lock_guard<std::mutex> lock(g_protector_mutex);
    hev_task_io_socket_set_protect_callback(nullptr);
    if (g_protector != nullptr) {
        env->DeleteGlobalRef(g_protector);
        g_protector = nullptr;
    }
    g_protect_method = nullptr;
    g_vm = nullptr;
}

int protect_socket(int fd) {
    std::lock_guard<std::mutex> lock(g_protector_mutex);
    if (g_vm == nullptr || g_protector == nullptr || g_protect_method == nullptr) return 0;
    JNIEnv* env = nullptr;
    bool attached = false;
    const jint env_result = g_vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    if (env_result == JNI_EDETACHED) {
        if (g_vm->AttachCurrentThread(&env, nullptr) != JNI_OK) return 0;
        attached = true;
    } else if (env_result != JNI_OK || env == nullptr) {
        return 0;
    }
    const jboolean result = env->CallBooleanMethod(g_protector, g_protect_method, fd);
    if (env->ExceptionCheck()) {
        env->ExceptionClear();
        if (attached) g_vm->DetachCurrentThread();
        return 0;
    }
    if (attached) g_vm->DetachCurrentThread();
    return result == JNI_TRUE ? 1 : 0;
}

void run_tunnel(const char* config_path, int tun_fd) {
    const int result = hev_socks5_tunnel_main_from_file(config_path, tun_fd);
    g_last_result.store(result, std::memory_order_release);
    g_running.store(false, std::memory_order_release);
}
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_darkmed_app_core_HevTun2Socks_startNative(JNIEnv* env, jobject, jstring config_path, jint tun_fd, jobject protector) {
    if (config_path == nullptr || protector == nullptr) return JNI_FALSE;
    const char* path = env->GetStringUTFChars(config_path, nullptr);
    if (path == nullptr) return JNI_FALSE;
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_running.load(std::memory_order_acquire) || g_worker.joinable()) {
        env->ReleaseStringUTFChars(config_path, path);
        return JNI_FALSE;
    }
    if (env->GetJavaVM(&g_vm) != JNI_OK) {
        env->ReleaseStringUTFChars(config_path, path);
        return JNI_FALSE;
    }
    const jclass protector_class = env->GetObjectClass(protector);
    g_protect_method = env->GetMethodID(protector_class, "protect", "(I)Z");
    if (g_protect_method == nullptr) {
        env->DeleteLocalRef(protector_class);
        g_vm = nullptr;
        env->ReleaseStringUTFChars(config_path, path);
        return JNI_FALSE;
    }
    g_protector = env->NewGlobalRef(protector);
    env->DeleteLocalRef(protector_class);
    if (g_protector == nullptr) {
        g_protect_method = nullptr;
        g_vm = nullptr;
        env->ReleaseStringUTFChars(config_path, path);
        return JNI_FALSE;
    }
    {
        std::lock_guard<std::mutex> protector_lock(g_protector_mutex);
        hev_task_io_socket_set_protect_callback(protect_socket);
    }
    g_last_result.store(0, std::memory_order_release);
    g_running.store(true, std::memory_order_release);
    try {
        g_worker = std::thread(run_tunnel, path, static_cast<int>(tun_fd));
    } catch (...) {
        g_running.store(false, std::memory_order_release);
        clear_protector(env);
        env->ReleaseStringUTFChars(config_path, path);
        return JNI_FALSE;
    }
    env->ReleaseStringUTFChars(config_path, path);
    return JNI_TRUE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_darkmed_app_core_HevTun2Socks_stopNative(JNIEnv* env, jobject) {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_worker.joinable()) {
        if (g_running.load(std::memory_order_acquire)) hev_socks5_tunnel_quit();
        std::thread worker = std::move(g_worker);
        worker.join();
    }
    g_running.store(false, std::memory_order_release);
    clear_protector(env);
    return JNI_TRUE;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_darkmed_app_core_HevTun2Socks_isRunningNative(JNIEnv*, jobject) {
    return g_running.load(std::memory_order_acquire) ? JNI_TRUE : JNI_FALSE;
}

extern "C" JNIEXPORT jlongArray JNICALL
Java_com_darkmed_app_core_HevTun2Socks_statsNative(JNIEnv* env, jobject) {
    std::size_t tx_packets = 0;
    std::size_t tx_bytes = 0;
    std::size_t rx_packets = 0;
    std::size_t rx_bytes = 0;
    hev_socks5_tunnel_stats(&tx_packets, &tx_bytes, &rx_packets, &rx_bytes);
    const jlong values[5] = {
        static_cast<jlong>(tx_packets),
        static_cast<jlong>(tx_bytes),
        static_cast<jlong>(rx_packets),
        static_cast<jlong>(rx_bytes),
        static_cast<jlong>(g_last_result.load(std::memory_order_acquire))
    };
    jlongArray result = env->NewLongArray(5);
    if (result == nullptr) return nullptr;
    env->SetLongArrayRegion(result, 0, 5, values);
    return result;
}
