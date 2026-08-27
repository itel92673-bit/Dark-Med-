DARKMED_LOCAL_PATH := $(call my-dir)
LOCAL_PATH := $(DARKMED_LOCAL_PATH)

include $(DARKMED_LOCAL_PATH)/hev-socks5-tunnel/Android.mk

LOCAL_PATH := $(DARKMED_LOCAL_PATH)
include $(CLEAR_VARS)
LOCAL_MODULE := darkmed-tun2socks-jni
LOCAL_SRC_FILES := darkmed_tun2socks_jni.cpp
LOCAL_C_INCLUDES := $(LOCAL_PATH)/hev-socks5-tunnel/src $(LOCAL_PATH)/hev-socks5-tunnel/third-part/hev-task-system/include
LOCAL_SHARED_LIBRARIES := hev-socks5-tunnel
LOCAL_CPPFLAGS += -std=c++17 -fexceptions -frtti
LOCAL_LDLIBS += -llog
LOCAL_LDFLAGS += -Wl,-z,max-page-size=16384 -Wl,-z,common-page-size=16384
include $(BUILD_SHARED_LIBRARY)
