#include <jni.h>
#include <opencv2/opencv.hpp>
#include <android/log.h>

#define LOG_TAG "NativeLib"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)

extern "C" {

// --------------------
// stringFromJNI
// --------------------
JNIEXPORT jstring JNICALL
Java_com_example_edgedetectionapp_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {

    LOGD("JNI connected successfully!");
    return env->NewStringUTF("JNI connected successfully!");
}

// --------------------
// processFrameJNI
// --------------------
JNIEXPORT jstring JNICALL
Java_com_example_edgedetectionapp_MainActivity_processFrameJNI(
        JNIEnv* env,
        jobject /* this */,
        jbyteArray frameData,
        jint width,
        jint height) {

    if (frameData == nullptr) {
        LOGD("Frame data is null!");
        return env->NewStringUTF("Frame data is null!");
    }

    jboolean isCopy;
    jbyte* bytes = env->GetByteArrayElements(frameData, &isCopy);
    if (!bytes) {
        LOGD("Failed to get byte array!");
        return env->NewStringUTF("Failed to get byte array!");
    }

    cv::Mat mat(height, width, CV_8UC4, reinterpret_cast<unsigned char*>(bytes));
    cv::Mat gray, edges;

    try {
        cv::cvtColor(mat, gray, cv::COLOR_RGBA2GRAY);
        cv::Canny(gray, edges, 50, 150);
    } catch (const cv::Exception& e) {
        env->ReleaseByteArrayElements(frameData, bytes, 0);
        LOGD("OpenCV exception: %s", e.what());
        return env->NewStringUTF(e.what());
    }

    env->ReleaseByteArrayElements(frameData, bytes, 0);
    LOGD("Frame processed successfully!");
    return env->NewStringUTF("Frame processed successfully!");
}

} // extern "C"
