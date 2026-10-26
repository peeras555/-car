name: Build APK

on:
  workflow_dispatch:
  push:
    branches: [ main, master ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17

      - uses: android-actions/setup-android@v3

      - uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: "8.7"

      - name: Build debug APK
        run: gradle --no-daemon assembleDebug

      - name: Build release APK (signed with debug key)
        run: gradle --no-daemon assembleRelease
        continue-on-error: true

      - uses: actions/upload-artifact@v4
        if: always()
        with:
          name: CarLauncher-apk
          path: app/build/outputs/apk/**/*.apk
<img width="1917" height="921" alt="image" src="https://github.com/user-attachments/assets/a01f7035-6eaf-4e9b-b648-00e4fce20e63" />
