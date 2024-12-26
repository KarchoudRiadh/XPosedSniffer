# DataSniffingApp

## Overview
DataSniffingApp is an Android app that hooks into popular network libraries (e.g., OkHttp, Volley, HttpURLConnection) to intercept and log API requests. It is designed for debugging and monitoring purposes in Android applications.

## Features
- Intercept network requests made by the target application.
- Log intercepted URLs into various formats (CSV, TXT, SQLite).
- Modular architecture following Clean Architecture principles.
- Supports dependency injection using Dagger Hilt.

## Architecture
The project is structured using Clean Architecture:

- **Domain Layer**: Contains core logic and abstractions (`UrlLogger`, `HookNetworkCallsUseCase`).
- **Data Layer**: Implements loggers (`FileUrlLogger`, `SQLiteUrlLogger`) and utilities.
- **Application Layer**: Handles framework-specific logic, hooks, and entry points.

## Setup
1. Clone the repository.
2. Ensure Xposed Framework is installed on your device.
3. Install the APK on your target device.

## Building
1. Add necessary dependencies using Gradle.
2. Build the project using Android Studio or `./gradlew build`.

## Testing
Run unit tests using:

```bash
./gradlew test
