# Photo Gallery App

## Overview
A modern Android photo gallery application that allows users to view, manage and interact with photos using a sleek, intuitive interface. This project demonstrates Android development using Jetpack Compose, Material 3 design principles, lazy loading, and gesture support.

## Features
- View photos in a responsive grid layout
- Display photos in full-screen detail view with zoom capabilities
- Add photos from camera or gallery
- Mark photos as favorites with a simple tap
- Swipe navigation between photos in detail view
- Delete unwanted photos
- Lazy loading and pagination for efficient performance

## Screenshots
![image](https://github.com/user-attachments/assets/cf821da1-e07f-46f9-b550-a73837745903)

![image](https://github.com/user-attachments/assets/20d57dba-c9e3-4d0c-8ac5-ad70cc82593b)


## Technologies Used
- Kotlin
- Jetpack Compose
- Material 3 Design
- Coil for image loading and caching
- Android SDK
- Gradle
- Kotlin Coroutines and Flow

## Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK version 24+
- Gradle 8.10+
- JDK 11+

## Setup Instructions

### Clone the Repository
```bash
git clone https://github.com/yourusername/PhotoGalleryApp.git
cd PhotoGalleryApp
```

### Open and Build in Android Studio
1. Open Android Studio
2. Select "Open an existing Android Studio project"
3. Navigate to the cloned repository and click "Open"
4. Wait for the project to sync and build
5. Connect an Android device or use the emulator

### Run the Application
- Click the "Run" button (green triangle) in Android Studio
- Select a deployment target (emulator or connected device)
- The app should install and launch automatically

## How to Use
1. Launch the Photo Gallery app
2. The main screen displays a grid of photos
3. Tap on any photo to view it in detail mode
4. In detail view:
   - Pinch to zoom in/out
   - Swipe left/right to navigate between photos
   - Tap the favorite icon to mark/unmark photos
5. From the main grid:
   - Long press on photos to access options (favorite/delete)
   - Use the floating action button to add new photos from camera or gallery
6. Access settings via the FAB menu

## Project Structure
```
app/
├── src/main/
│   ├── java/com/example/photo/
│   │   ├── MainActivity.kt        # Main activity and navigation
│   │   ├── data/
│   │   │   ├── Photo.kt           # Photo data model
│   │   │   └── PhotoDataSource.kt # Sample data provider
│   │   ├── ui/screens/
│   │   │   ├── PhotoGridScreen.kt # Grid display of photos
│   │   │   └── PhotoDetailScreen.kt # Detail view with zoom/navigation
│   │   ├── viewmodel/
│   │   │   └── PhotoViewModel.kt  # Business logic and state management
│   │   └── ui/theme/              # Theme configuration
│   ├── res/
│   │   ├── drawable/              # App icons and resources
│   │   ├── values/
│   │   │   ├── colors.xml         # Color definitions
│   │   │   ├── strings.xml        # String resources
│   │   │   └── themes.xml         # App theme definitions
│   └── AndroidManifest.xml        # App configuration with camera permissions
└── build.gradle.kts               # App module build configuration
```

## Contributing
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Learning Resources
- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material 3 Design](https://m3.material.io/)
- [Kotlin Programming Language](https://kotlinlang.org/docs/home.html)
- [Android Developers Documentation](https://developer.android.com/docs)
- [Coil Image Loading Library](https://coil-kt.github.io/coil/)

## Permissions Used
- `android.permission.READ_MEDIA_IMAGES` - Required to access photos from gallery
- `android.permission.READ_EXTERNAL_STORAGE` - For backward compatibility
- `android.permission.CAMERA` - Required to take photos with the camera

## Acknowledgements
- Android Developers documentation and tutorials
- Material Design for UI inspiration
- Jetpack Compose samples for implementation patterns
- Coil library for efficient image loading

---
*This app demonstrates modern Android development techniques using Jetpack Compose and Material 3 design principles while providing an intuitive and performant photo gallery experience.*

Similar code found with 1 license type
