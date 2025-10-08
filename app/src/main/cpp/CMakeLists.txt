cmake_minimum_required(VERSION 3.22.1)
project("edgedetectionapp")

# 1. Add native library
add_library(
        native-lib
        SHARED
        native-lib.cpp
)

# 2. Include OpenCV headers
include_directories(
        ${CMAKE_SOURCE_DIR}/OpenCV-android-sdk/sdk/native/jni
)

# 3. Import OpenCV prebuilt .so libraries for arm64-v8a
add_library(opencv_core SHARED IMPORTED)
set_target_properties(opencv_core PROPERTIES
        IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/OpenCV-android-sdk/sdk/native/libs/arm64-v8a/libopencv_core.so
)

add_library(opencv_imgproc SHARED IMPORTED)
set_target_properties(opencv_imgproc PROPERTIES
        IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/OpenCV-android-sdk/sdk/native/libs/arm64-v8a/libopencv_java4.so
)

# 4. Find Android log library
find_library(log-lib log)

# 5. Link libraries
target_link_libraries(
        native-lib
        opencv_core
        opencv_imgproc
        ${log-lib}
        c++_static
)
