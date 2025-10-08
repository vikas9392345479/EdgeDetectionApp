#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_edgedetectionapp_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string message = "✅ JNI Connected Successfully!";
    return env->NewStringUTF(message.c_str());
}

