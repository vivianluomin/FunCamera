package com.example.asus1.funcamera.RecordVideo.RecordUtil;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SplitVideoThread extends Thread {

    private static final String TAG = "SplitVideoThread";

    private String mVideoPath;
    private String mMusicPath;
    private long mMusicTime;
    private Handler mMainHandler;

    public SplitVideoThread(String name,String videoPath,String musicPath,
                            long musicTime,Handler mainHandler) {
        //Log.d(TAG, "SplitVideoThread: ");
        super(name);
        mVideoPath = videoPath;
        mMusicPath = musicPath;
        mMusicTime = musicTime;
        mMainHandler = mainHandler;
    }



    @Override
    public void run() {

       String video =  slitpeVideo(mVideoPath,mMusicPath,mMusicTime);

        Message message = mMainHandler.obtainMessage(1,video);
        mMainHandler.sendMessage(message);


    }


    private   String slitpeVideo(String videoPath,String musicPath,long musicTime){


        String outputVideo = VideoMediaMuxer.getCaptureFile
                (Environment.DIRECTORY_MOVIES,".mp4").toString();


        try {

            MediaMuxer videoMediaMuxer = new MediaMuxer(outputVideo,
                    MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int videoTracIndex = -1;
            int musicTracIndex = -1;
            MediaExtractor videoExtractor = new MediaExtractor();
            videoExtractor.setDataSource(videoPath);

            for(int i = 0;i<videoExtractor.getTrackCount();i++) {
                MediaFormat format = videoExtractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("video/")) {
                    videoExtractor.selectTrack(i);
                    videoTracIndex = videoMediaMuxer.
                            addTrack(videoExtractor.getTrackFormat(i));
                    break;

                }

            }


            MediaExtractor musicExtractor = new MediaExtractor();
            musicExtractor.setDataSource(musicPath);


            MediaCodec.BufferInfo videoInfo = new MediaCodec.BufferInfo();
            videoInfo.presentationTimeUs = 0;
            MediaCodec.BufferInfo audioInfo = new MediaCodec.BufferInfo();
            audioInfo.presentationTimeUs = 0;

            MediaCodec musicCodec = convertMusic(musicTime,musicExtractor);

            musicTracIndex = readSample(musicExtractor,musicCodec,videoMediaMuxer,
                    audioInfo,musicTracIndex,false);


            if(videoTracIndex!=-1&&musicTracIndex!=-1){

                Log.d(TAG, "slitpeVideo: ");

                ByteBuffer byteBuffer = ByteBuffer.allocate(1024*1024);
                long startMS = System.currentTimeMillis();


                while (true){
                    int videoSampleSize =
                            videoExtractor.readSampleData(byteBuffer,0);
                    Log.d(TAG, "slitpeVideo: "+videoSampleSize);

                    if(videoSampleSize<0){
                        videoInfo.flags = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                        videoInfo.offset = 0;
                        videoInfo.size = 0;
                        videoInfo.presentationTimeUs = getPTSUs();
                        prevOutputPTSUs = videoInfo.presentationTimeUs;

                        videoMediaMuxer.writeSampleData(videoTracIndex,byteBuffer,videoInfo);

                        audioInfo.flags = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
                        audioInfo.offset = 0;
                        audioInfo.size = 0;
                        audioInfo.presentationTimeUs = prevOutputPTSUs;
                        readSample(musicExtractor,musicCodec,videoMediaMuxer
                                ,audioInfo,musicTracIndex,true);
                        readSample(musicExtractor,musicCodec,videoMediaMuxer
                                ,audioInfo,musicTracIndex,false);

                        break;
                    }


                    byteBuffer.position(videoSampleSize);
                    byteBuffer.flip();
                    videoInfo.flags = videoExtractor.getSampleFlags();
                    videoInfo.presentationTimeUs = getPTSUs();
                    prevOutputPTSUs = videoInfo.presentationTimeUs;
                    videoInfo.size = videoSampleSize;
                    videoMediaMuxer.writeSampleData(videoTracIndex,byteBuffer,videoInfo);
                    byteBuffer.clear();

                    audioInfo.presentationTimeUs = prevOutputPTSUs;

                    readSample(musicExtractor,musicCodec,videoMediaMuxer,audioInfo,
                            musicTracIndex,false);


                    videoExtractor.advance();
                    musicExtractor.advance();

                    while (prevOutputPTSUs/1000>System.currentTimeMillis()){

                        try {
                            sleep(10);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                            break;
                        }
                    }

                }

                videoMediaMuxer.stop();
                videoExtractor.release();
                musicExtractor.release();
                videoMediaMuxer.release();
                return outputVideo;
            }


        }catch (IOException e){

        }


        return null;

    }



    private static MediaCodec convertMusic(long time,
                                           MediaExtractor musicExtractor ){

        try {

            MediaFormat musicFormat =null;
            for(int i = 0;i<musicExtractor.getTrackCount();i++){
                MediaFormat format = musicExtractor.getTrackFormat(i);
                if(format.getString(MediaFormat.KEY_MIME).startsWith("audio/")){
                    musicExtractor.selectTrack(i);
                    musicFormat = format;
                    break;

                }
            }

            if(musicFormat!=null){

                MediaFormat audioFormat = MediaFormat.createAudioFormat(AudioRecordEncode.MIME_TYPE,
                        AudioRecordEncode.AUDIO_SAMPLE_RATE,AudioRecordEncode.AUDIO_CHANNEL);
                audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
                audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
                audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, AudioRecordEncode.BIT_RATE);
                audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);

                MediaCodec codec =  MediaCodec.createEncoderByType("audio/mp4a-latm");
                codec.configure(audioFormat,null, null,
                        MediaCodec.CONFIGURE_FLAG_ENCODE);
                codec.start();

                musicExtractor.seekTo(time/3,MediaExtractor.SEEK_TO_NEXT_SYNC);

                return codec;
            }


        }catch (IOException e){

        }

        return null;

    }



    private static int readSample(MediaExtractor extractor,
                                  MediaCodec codec,
                                  MediaMuxer mediaMuxer,
                                  MediaCodec.BufferInfo bufferInfo,
                                  int tracker,
                                  boolean eos){

        int index;
        int trackerId = tracker;

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024*1024);

        int size = extractor.readSampleData(byteBuffer,0);

        bufferInfo.size = size;
        bufferInfo.offset=0;
        bufferInfo.flags = extractor.getSampleFlags();
        index = codec.dequeueInputBuffer(1000);
        //Log.d(TAG, "convertMusic: index:   "+index);

        ByteBuffer inputBuffer = codec.getInputBuffer(index);
        inputBuffer.clear();

        if(byteBuffer!=null){
            inputBuffer.put(byteBuffer);
            inputBuffer.position(size);
            inputBuffer.flip();
        }

        if(size<=0 ||eos){

            codec.queueInputBuffer(index,0,0,
                    bufferInfo.presentationTimeUs,MediaCodec.BUFFER_FLAG_END_OF_STREAM);
            return trackerId;
        }else {
            codec.queueInputBuffer(index,0,size,
                    bufferInfo.presentationTimeUs,0);
        }

        boolean wait = true;

        LOOP:   while (wait){
            int encodeStatue = codec.
                    dequeueOutputBuffer(bufferInfo,1000);
            if(encodeStatue == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                trackerId= mediaMuxer.addTrack(codec.getOutputFormat());
                mediaMuxer.start();

                return trackerId;
            }else if(encodeStatue == MediaCodec.INFO_TRY_AGAIN_LATER){

                break LOOP;

            }else if(encodeStatue<=0){


            } else {

                ByteBuffer byteBuffer1 = codec.getOutputBuffer(encodeStatue);
                mediaMuxer.writeSampleData(trackerId,byteBuffer1,bufferInfo);
                codec.releaseOutputBuffer(encodeStatue,false);
                wait = false;

            }

        }


        return trackerId;
    }

    /**
     * previous presentationTimeUs for writing
     */
    private long prevOutputPTSUs = 0;
    /**
     * get next encoding presentationTimeUs
     * @return
     */
    protected long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        return result;
    }

}
