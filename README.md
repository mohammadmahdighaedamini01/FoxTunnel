# FoxTunnel

A clean, modern VPN client for Android with a fresh light UI, country flags, and favourite locations.

> **Disclaimer:** FoxTunnel is an unofficial, community project. It is not affiliated with, endorsed by, or sponsored by Mozilla or any VPN provider.

<!-- Add screenshots here, e.g.
<p align="center">
  <img src="screenshots/home.png" width="250" />
  <img src="screenshots/locations.png" width="250" />
</p>
-->

## Features

- One-tap connect / disconnect with a smooth animated power button
- Location list with country flags
- Shows your current location at a glance
- Star your favourite locations for quick access
- Custom light theme (orange `#F97810` and magenta `#AB327F`)
- Supports Android 10 and above
- Small, arm64-only release build

## Download

Ready-to-install APKs are available on the [Releases](../../releases) page.

1. Download the latest `.apk` file.
2. Allow installation from unknown sources if Android asks.
3. Open the file and install.

## Build from source

### Requirements

- [Android Studio](https://developer.android.com/studio) (latest stable version)
- JDK 17 or newer (the one bundled with Android Studio works)
- Android SDK installed through Android Studio

### Steps

1. Clone the repository:

   ```bash
   git clone https://github.com/<your-username>/FoxTunnel.git
   cd FoxTunnel
   ```

2. Open the folder in Android Studio and wait for the Gradle sync to finish.

3. Build a debug APK:

   ```bash
   ./gradlew assembleDebug        # Linux / macOS
   gradlew.bat assembleDebug      # Windows
   ```

   The APK will be created in `app/build/outputs/apk/debug/`.

### Release build

Release builds must be signed with **your own** keystore. Create one in Android Studio
(*Build > Generate Signed App Bundle / APK*) and keep it outside the repository.

> Never commit your keystore (`.jks`, `.keystore`, or similar) or any passwords to Git.

## Project structure

```
FoxTunnel/
├── app/                    # Android application module
├── gradle/                 # Gradle wrapper files
├── build.gradle.kts        # Root build script
├── settings.gradle.kts     # Project settings
└── gradle.properties       # Gradle configuration
```

## Roadmap

- [ ] Windows desktop version
- [ ] More UI customization options

## Contributing

Issues and pull requests are welcome. For larger changes, please open an issue first to discuss what you would like to change.

## Acknowledgements

This project is based on the open-source [FoxyVPN](https://github.com/Vauth/FoxyVPN) by Vauth. Thanks to the original author and all contributors.

## License

Add a license for your project (for example MIT) and place it in a `LICENSE` file.
Make sure it is compatible with the license of the original project mentioned above.

## Author

Made by **Nightmare**.
