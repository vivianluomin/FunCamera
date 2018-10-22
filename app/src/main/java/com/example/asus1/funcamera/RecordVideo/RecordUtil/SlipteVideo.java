package com.example.asus1.funcamera.RecordVideo.RecordUtil;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SlipteVideo {

    private static final String TAG = "SlipteVideo";

    public static String slitpeVideo(String videoPath,String musicPath,int musicLegth){

        String outputVideo = VideoMediaMuxer.getCaptureFile
                (Environment.DIRECTORY_MOVIES,".mp4").toString();

        String music = ConvertMusic(musicPath);
        if(music==null){
            return null;
        }

        try {

            MediaMuxer mediaMuxer = new MediaMuxer(outputVideo,
                    MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            int videoTracIndex = -1;

            MediaExtractor extractor = new MediaExtractor();
            extractor.setDataSource(videoPath);
            for(int i = 0;i<extractor.getTrackCount();i++){
                MediaFormat format = extractor.getTrackFormat(i);
                String mime = format.getString(MediaFormat.KEY_MIME);
                if(mime.startsWith("video/")){
                    extractor.selectTrack(i);
                    videoTracIndex = mediaMuxer.addTrack(format);
                    break;

                }

            }

            MediaExtractor musicExtractor = new MediaExtractor();
            musicExtractor.setDataSource(music);
            int musicTrackIndex = -1;
            for(int i = 0;i<musicExtractor.getTrackCount();i++){
                MediaFormat format = musicExtractor.getTrackFormat(i);
                if(format.getString(MediaFormat.KEY_MIME).startsWith("audio/")){
                    musicExtractor.selectTrack(i);
                    musicTrackIndex = mediaMuxer.addTrack(format);
                    break;

                }
            }

            mediaMuxer.start();

            if(videoTracIndex!=-1&&musicTrackIndex!=-1){

                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                info.presentationTimeUs = 0;
                ByteBuffer byteBuffer = ByteBuffer.allocate(100*1024);
                while (true){
                    int videoSampleSize = extractor.readSampleData(byteBuffer,0);

                    if(videoSampleSize<0){
                        break;
                    }

                    info.offset = 0;
                    info.size = videoSampleSize;
                    info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                    info.presentationTimeUs = extractor.getSampleTime();
                    mediaMuxer.writeSampleData(videoTracIndex,byteBuffer,info);

                    int sampleSize = musicExtractor.readSampleData(byteBuffer,0);

                    info.offset = 0;
                    info.size = sampleSize;
                    info.flags = MediaCodec.BUFFER_FLAG_SYNC_FRAME;
                    info.presentationTimeUs = musicExtractor.getSampleTime();
                    mediaMuxer.writeSampleData(musicTrackIndex,byteBuffer,info);

                    extractor.advance();
                    musicExtractor.advance();
                }
            }

            extractor.release();
            musicExtractor.release();
            mediaMuxer.stop();
            mediaMuxer.release();

            File file = new File(music);
            file.delete();

            return outputVideo;

        }catch (IOException e){

        }


        return null;

    }

    public static String ConvertMusic(String musicPath){

        String outputMusic = VideoMediaMuxer.getCaptureFile
                (Environment.DIRECTORY_MOVIES,".mp3").toString();

        try {
            MediaExtractor musicExtractor = new MediaExtractor();
            musicExtractor.setDataSource(musicPath);
            MediaMuxer mediaMuxer = new MediaMuxer(outputMusic,
                    MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

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

                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                bufferInfo.presentationTimeUs = 0;
                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                int index;
                int tracker =-1;
                long presentationTimeUs;
                 while (true){
                    int size = musicExtractor.readSampleData(byteBuffer,0);
                    presentationTimeUs = musicExtractor.getSampleTime();
                     Log.d(TAG, "ConvertMusic: "+presentationTimeUs);
                    bufferInfo.presentationTimeUs = presentationTimeUs;

                    index = codec.dequeueInputBuffer(-1);

                    ByteBuffer inputBuffer = codec.getInputBuffer(index);
                     inputBuffer.clear();

                     if(byteBuffer!=null){
                         inputBuffer.put(byteBuffer);
                     }

                     if(size<=0){

                         codec.queueInputBuffer(index,0,0,
                                 presentationTimeUs,MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                         break;
                     }else {
                         codec.queueInputBuffer(index,0,size,
                                 presentationTimeUs,0);
                     }

                     int encodeStatue = codec.
                             dequeueOutputBuffer(bufferInfo,10000);
                     if(encodeStatue == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
                        tracker= mediaMuxer.addTrack(codec.getOutputFormat());
                         mediaMuxer.start();
                     }else {

                         ByteBuffer byteBuffer1 = codec.getOutputBuffer(encodeStatue);
                         mediaMuxer.writeSampleData(tracker,byteBuffer1,bufferInfo);
                     }

                     musicExtractor.advance();
                 }

                 codec.stop();
                 codec.release();
            }

            musicExtractor.release();
            mediaMuxer.stop();
            mediaMuxer.release();
            return outputMusic;

        }catch (IOException e){

        }


        return null;

    }
}
