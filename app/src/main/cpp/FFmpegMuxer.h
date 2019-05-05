//
// Created by luomin on 2019/4/28.
//

#ifndef FUNCAMERA_FFMPEGMUXER_H
#define FUNCAMERA_FFMPEGMUXER_H
#include <jni.h>
#include <string>
#include<android/log.h>

extern "C"
{

#include <android/native_window_jni.h>
#include <libavfilter/avfilter.h>
#include <libavcodec/avcodec.h>
//封装格式处理
#include <libavformat/avformat.h>
//像素处理
#include <libswscale/swscale.h>
#include <unistd.h>

}

#define TAG "FFmpegMxuer"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__) // 定义LOGD类型
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__) // 定义LOGI类型
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__) // 定义LOGW类型
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__) // 定义LOGE类型
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__) // 定义LOGF类型


using namespace std;

class BufferInfo {
public:
    BufferInfo(){

    }
    int offset;
    int size;
    int64_t presentationTimeUs;
    int flags;
    static const int BUFFER_FLAG_END_OF_STREAM = 4;
    static const int BUFFER_FLAG_KEY_FRAME = 1;
};

class FFmpegMuxer {

public:
    FFmpegMuxer(const char *path);
    void writeData(int mediaTrack,uint8_t *data,BufferInfo *info);
    void stop();


private:
    const char* mPath;
    AVStream *mVideoStream;
    AVStream *mAudioStream;
    AVFormatContext *mFormateContext = NULL;
    AVFormatContext *mFormateContext_audio = NULL;
    AVFormatContext *mFormateContext_video = NULL;
    int mVideroIndex = 0;
    int mAudioIndex = 0;
    void init();
    void writeToFile(uint8_t *data,BufferInfo *info,AVStream *avStreamm,int index);
    AVStream *addVideoStream(AVFormatContext *pForContext,AVCodecID codecID);
    AVStream *addAudioStream(AVFormatContext *pForContext,AVCodecID codecID);


};




#endif //FUNCAMERA_FFMPEGMUXER_H
