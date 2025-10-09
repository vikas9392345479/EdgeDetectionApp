# 🧠 Edge Detection App (Android + OpenCV + JNI Integration)

This project demonstrates **native C++ image processing using OpenCV** integrated with **Android** through **JNI (Java Native Interface)**.
It’s part of the **Flam Bengaluru R&D Internship Assessment**, showcasing cross-platform engineering and research-oriented development skills.

---

## 🚀 Features Implemented

✅ **Native-C++ Integration (JNI)**

* Real-time communication between Java and native C++ layer.
* C++ handles image processing logic for better speed and efficiency.

✅ **OpenCV Integration**

* Implemented edge detection and image transformation pipeline using OpenCV 4.12.0.
* Verified correct linking and functioning through JNI and native libraries.

✅ **Android App Layer**

* Simple, clean Android UI built using XML layouts.
* Displays processed results from native C++ code.

✅ **Documentation & Commits**

* 9 clean commits with clear history and descriptive messages.
* Proper CMake, Gradle, and native source folder structure.

---

## 🧪 Research Components (Planned/Partially Implemented)

🟡 **OpenGL Rendering (Next Step)**

* Future addition for GPU-accelerated visualization of processed frames.

🟡 **TypeScript Web Viewer**

* Planned web-based viewer to visualize processed output using TypeScript + WebAssembly.

---

## ⚙️ Setup Instructions

### 📦 Requirements

* Android Studio (latest version)
* NDK 26+
* OpenCV Android SDK (4.12.0)

### 🧩 Steps

1. Clone this repository

   ```bash
   git clone https://github.com/<your-username>/EdgeDetectionApp.git
   ```
2. Extract and place the OpenCV SDK under:

   ```
   app/OpenCV-android-sdk/
   ```
3. Clean and rebuild the project:

   ```
   Build → Clean Project  
   Build → Rebuild Project
   ```
4. Connect a physical device or start an emulator.
5. Run the app — it should show processed output from native OpenCV code.

---

## 🧠 Architecture Overview

### 🔹 Android (Java/Kotlin)

* Handles UI and camera preview (Activity layer).

### 🔹 JNI Bridge

* Transfers image frame data between Android and C++ layer.

### 🔹 C++ / OpenCV Core

* Performs edge detection, image processing, and sends results back.

### 🔹 (Planned) OpenGL Layer

* For future GPU-based rendering.

### 🔹 (Planned) Web Viewer (TypeScript)

* Displays processed results through a lightweight web UI.

---

## 🖼️ Screenshots

https://image2url.com/images/1759944830838-8c694164-0a0a-471b-9cec-65b6e0921e1a.png
---

## 📚 Folder Structure

```
EdgeDetectionApp/
│
├── app/
│   ├── src/main/
│   │   ├── cpp/              # C++ native code
│   │   ├── java/             # Android Java code
│   │   └── res/layout/       # UI XML files
│   └── CMakeLists.txt        # Native build config
│
├── images/                   # Screenshots for README
├── README.md
└── .gitignore
```

---



---

