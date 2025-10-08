# EdgeDetectionApp

A real-time edge detection application built for Android with optional web integration. Uses OpenCV via JNI for fast image processing.

---

## Features

✅ Real-time edge detection from camera frames  
✅ JNI integration for native C++ processing  
✅ Uses OpenCV for computer vision operations  
✅ Quick frame-to-frame processing  
✅ Android and Web components ready

---

![Screenshot]https://image2url.com/images/1759944830838-8c694164-0a0a-471b-9cec-65b6e0921e1a.png

## Setup Instructions

1. **Install Android Studio** (version 4.1 or higher recommended)
2. **Install NDK** via Android Studio:
    - Go to `Tools > SDK Manager > SDK Tools`
    - Check **NDK (Side by side)**
3. **Copy OpenCV Android SDK**:
    - Place it in `app/src/main/cpp/OpenCV-android-sdk`
    - Ensure `CMakeLists.txt` points to the correct path:
      ```cmake
      set(OpenCV_DIR "app/OpenCV-android-sdk/sdk/native/jni")
      ```
4. **Sync Gradle & Build Project**
5. **Run on Android Device** with camera permission

---

## Architecture Overview

### Android
- **MainActivity** captures camera frames using Camera2 API.
- Frames are converted to `ByteArray` and passed to native C++ via JNI.

### JNI / C++
- `native-lib.cpp` receives camera frames from Kotlin.
- OpenCV processes the frames:
    - Converts RGBA to Grayscale
    - Applies Canny edge detection
- Returns a status string to Android.

### Optional Web / TypeScript Component
- Can be extended to send frames to a web interface.
- Uses TypeScript for UI and data flow.

---

## Notes
- Make sure to allow **Camera permission** at runtime.
- Tested on Android API level 24+.
- OpenCV version used: **4.5+**

---

## License

MIT License
