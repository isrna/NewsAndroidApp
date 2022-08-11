#include <jni.h>
#include <string>
#include "RandomQuotes.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_isrna_newsapp_feature_1article_1list_ArticleListActivity_GetRandomQuoteString(
        JNIEnv* env,
        jobject /* this */) {
    int range = RandomQuotes::quotesList.size() - 0 + 1;
    int num = rand() % range + 0;

    std::string hello = RandomQuotes::quotesList.at(num);
    return env->NewStringUTF(hello.c_str());
}