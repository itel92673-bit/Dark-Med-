# External sources used for Dark Med QA

- Firebase Test Lab CLI and device catalog: https://firebase.google.com/docs/test-lab/android/command-line
- Google Cloud CLI authentication: https://docs.cloud.google.com/sdk/docs/authorizing
- AWS CLI credential/configuration files: https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html
- AWS Device Farm Android instrumentation: https://docs.aws.amazon.com/devicefarm/latest/developerguide/test-types-android-instrumentation.html
- AWS Device Farm IAM policy examples: https://docs.aws.amazon.com/devicefarm/latest/developerguide/security-iam-id-based-policy-examples.html
- BrowserStack App Automate app upload API: https://www.browserstack.com/docs/app-automate/api-reference/appium/apps
- Kobiton API documentation: https://api.kobiton.com/v2/docs
- Kobiton app upload guide: https://docs.kobiton.com/apps/upload-an-app/using-the-kobiton-api-postman
- Sauce Labs Appium real devices: https://docs.saucelabs.com/mobile-apps/automated-testing/appium/real-devices/
- Sauce Labs mobile app storage: https://docs.saucelabs.com/mobile-apps/app-storage/
- Perfecto Appium: https://help.perfecto.io/perfecto-help/content/perfecto/automation-testing/appium.htm
- Perfecto security tokens: https://help.perfecto.io/perfecto-help/content/perfecto/automation-testing/generate_security_tokens.htm
- Claude Code authentication: https://code.claude.com/docs/en/authentication
- Claude Code quickstart: https://code.claude.com/docs/en/quickstart
- Anthropic CLI quickstart: https://platform.claude.com/docs/en/cli-sdks-libraries/cli/quickstart
- MobSFscan official repository: https://github.com/MobSF/mobsfscan

Verified facts used: Test Lab supports gcloud instrumentation and device catalog queries; gcloud supports user login and workload credentials; AWS CLI stores profiles/SSO/temporary credentials; BrowserStack upload uses Basic Auth and returns a bs:// app URL; Kobiton API uses Basic Auth; Sauce requires account username/access key for Appium real-device usage; Perfecto uses cloud/security-token/Appium configuration; Claude Code has official CLI/auth documentation but no local `claude` executable was found; no official OX Alpha integration was verified.

MobSF review sources added for current report:

- Android Play Integrity overview: https://developer.android.com/google/play/integrity/overview
- Android Network Security Configuration: https://developer.android.com/privacy-and-security/security-config
- Android security with TLS/SSL and certificate pinning: https://developer.android.com/privacy-and-security/security-ssl
- MobSFscan source scanner: https://github.com/MobSF/mobsfscan

Verified points used: Play Integrity is designed for app/user action and server-request integrity decisions and returns app/device signals; it is not a local-only replacement for a backend decision. Android Network Security Configuration supports cleartext policy, certificate transparency, and domain-specific pinning; CT availability varies by API level and Android documentation warns about pinning rotation/backup-key requirements. The current MobSFscan command accepts Android source paths and does not provide full MobSF Server APK/Dynamic Analysis.
