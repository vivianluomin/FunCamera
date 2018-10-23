package com.example.asus1.funcamera.RecordVideo.RecordUtil;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class SlipteVideo {

    private static final String TAG = "SlipteVideo";
    private static int count = 0;

    public static String slitpeVideo(String videoPath,String musicPath,long musicTime){

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
            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
            info.presentationTimeUs = 0;

            MediaCodec musicCodec = convertMusic(musicTime,musicExtractor);

            musicTracIndex = readSample(musicExtractor,musicCodec,videoMediaMuxer,
                    info,musicTracIndex,false);


            if(videoTracIndex!=-1&&musicTracIndex!=-1){


                ByteBuffer byteBuffer = ByteBuffer.allocate(1024*1024);
                while (true){
                    int videoSampleSize =
                            videoExtractor.readSampleData(byteBuffer,0);

                    if(videoSampleSize<0){
//                        info.flags = MediaCodec.BUFFER_FLAG_END_OF_STREAM;
//                        info.offset = 0;
//                        info.size = 0;
//                        info.presentationTimeUs = videoExtractor.getSampleTime();
//                        videoMediaMuxer.writeSampleData(videoTracIndex,byteBuffer,info);
                        readSample(musicExtractor,musicCodec,videoMediaMuxer
                                ,info,musicTracIndex,true);
                        readSample(musicExtractor,musicCodec,videoMediaMuxer
                                ,info,musicTracIndex,false);
                        break;
                    }
                    info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                    prevOutputPTSUs = info.presentationTimeUs;
                    info.presentationTimeUs =getPTSUs();

                    videoMediaMuxer.writeSampleData(videoTracIndex,byteBuffer,info);
                    readSample(musicExtractor,musicCodec,videoMediaMuxer,info,
                            musicTracIndex,false);


                    videoExtractor.advance();
                    musicExtractor.advance();
                }

                videoExtractor.release();
                musicExtractor.release();
                videoMediaMuxer.stop();
                videoMediaMuxer.release();
                return outputVideo;
            }

//            File file = new File(music);
//            file.delete();


        }catch (IOException e){

        }


        return null;

    }

    private static MediaCodec converVideo(MediaExtractor videoExtractor){

        for(int i = 0;i<videoExtractor.getTrackCount();i++) {
            MediaFormat format = videoExtractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                videoExtractor.selectTrack(i);
                break;

            }

        }

        try {
            MediaCodec mViedeoEncode = MediaCodec.createEncoderByType(VideoRecordEncode.MIME_TYPE);
            MediaFormat format =  MediaFormat.createVideoFormat(VideoRecordEncode.MIME_TYPE,
                    1280,720);
            format.setInteger(MediaFormat.KEY_BIT_RATE,calcBitRate(1280,720));
            format.setInteger(MediaFormat.KEY_FRAME_RATE,VideoRecordEncode.FRAME_RATE);
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,10);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            mViedeoEncode.configure(format,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);

            mViedeoEncode.start();
            return mViedeoEncode;

        }catch (IOException e){

        }



        return null;
    }

    private static int calcBitRate(int width,int height) {
        final int bitrate = (int)(VideoRecordEncode.BPP *
                VideoRecordEncode.FRAME_RATE * width * height);
        Log.i(TAG, String.format("bitrate=%5.2f[Mbps]", bitrate / 1024f / 1024f));
        return bitrate;
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

                musicExtractor.seekTo(time/2,MediaExtractor.SEEK_TO_NEXT_SYNC);

                return codec;
            }


        }catch (IOException e){

        }

        return null;

    }

    private static int readSample(MediaExtractor musicExtractor,
                                      MediaCodec codec,
                                      MediaMuxer mediaMuxer,
                                      MediaCodec.BufferInfo bufferInfo,
                                      int musicTracker,
                                      boolean eos){

            int index;
            int tracker = musicTracker;
            long presentationTimeUs;
            ByteBuffer byteBuffer = ByteBuffer.allocate(100*1024);

            int size = musicExtractor.readSampleData(byteBuffer,0);
           // presentationTimeUs = musicExtractor.getSampleTime();
            //Log.d(TAG, "convertMusic: "+presentationTimeUs);
//            if(presentationTimeUs!=-1){
//                bufferInfo.presentationTimeUs = presentationTimeUs;
//            }


            index = codec.dequeueInputBuffer(-1);
            Log.d(TAG, "convertMusic: index:   "+index);

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
                return tracker;
            }else {
                codec.queueInputBuffer(index,0,size,
                        bufferInfo.presentationTimeUs,0);
            }

            boolean wait = true;

            LOOP:   while (wait){
                int encodeStatue = codec.
                        dequeueOutputBuffer(bufferInfo,10000);
                if(encodeStatue == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                    tracker= mediaMuxer.addTrack(codec.getOutputFormat());
                    mediaMuxer.start();


                    return tracker;
                }else if(encodeStatue == MediaCodec.INFO_TRY_AGAIN_LATER){

                    break LOOP;

                }else if(encodeStatue<=0){


                } else {
                    ByteBuffer byteBuffer1 = codec.getOutputBuffer(encodeStatue);
                    mediaMuxer.writeSampleData(tracker,byteBuffer1,bufferInfo);
                    codec.releaseOutputBuffer(encodeStatue,false);
                    wait = false;

                }

            }


        return tracker;
    }

    private static long prevOutputPTSUs = 0;
    /**
     * get next encoding presentationTimeUs
     * @return
     */
    protected static long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        return result;
    }
}
