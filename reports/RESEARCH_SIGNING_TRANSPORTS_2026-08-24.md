# Signing, obfs4, and Snowflake Decision Record

## APK signing

Android requires an APK to be digitally signed before installation. Android supports APK signature schemes v1, v2, and v3; AOSP recommends the compatible combination according to the supported platform range, with v2/v3 providing whole-file integrity protection on modern Android [1]. The current Dark Med release variant passes `apksigner verify` using v2, but its certificate subject is `C=US, O=Android, CN=Android Debug`; it is therefore a QA artifact, not a production release.

A production signing key must be generated and retained by the owner outside source control. The safe workflow is to create a user-owned `.jks` keystore, configure a release signing configuration through environment variables or a local untracked properties file, build `assembleRelease`, verify the certificate fingerprint, and keep the private keystore and passwords out of chat and out of the APK/repository. The Android documentation distinguishes an app-signing key from an upload key and recommends protecting keys and separating upload from app signing where Play App Signing is used [2]. No private key was requested or generated in the project during this cycle.

## obfs4 and Snowflake

The current APK contains `libtor.so` and the Tor configuration accepts bridge/transport syntax, but the archive contains no obfs4 or Snowflake transport executable/library. This is why the auditor emits two warnings. Guardian Project describes Android PT integration as a separate layer involving an actual PT implementation such as obfs4, bridge parameters, and the SOCKS/proxy integration; configuration syntax alone is not an operational PT implementation [3].

Snowflake is a Tor pluggable transport that uses WebRTC and volunteer proxies. The Tor Project states that it is embedded in Tor-powered applications such as Tor Browser and Orbot, and that application integration requires the actual transport implementation and its runtime behavior [4]. Adding a string, a bridge line, or an unrelated binary would not prove support and could create an unsafe or non-working route.

## Required implementation path

To remove the warnings legitimately, the project must select a maintained Android-compatible PT implementation and its license, obtain the exact native/Go/Rust artifacts or build them reproducibly for all required ABIs, package the binaries in the expected Tor PT layout, pass the Tor managed transport environment contract, verify process startup and IPC, and run a real bridge/bootstrap test on the target network. The same procedure is required for Snowflake, with additional WebRTC/native integration and lifecycle/resource testing. Until those steps are completed, the correct status remains `CONFIGURATION_SUPPORTED` or `NOT IMPLEMENTED`, not PASS.

## References

[1]: https://source.android.com/docs/security/features/apksigning "Android Open Source Project — App signing"
[2]: https://developer.android.com/studio/publish/app-signing "Android Developers — Sign your app"
[3]: https://guardianproject.info/2019/04/16/exploring-possibilities-of-pluggable-transports-on-android/ "Guardian Project — Exploring possibilities of Pluggable Transports on Android"
[4]: https://snowflake.torproject.org/ "Tor Project — Snowflake"
