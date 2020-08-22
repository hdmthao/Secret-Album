package com.bigocoding.secretalbum;

import android.media.MediaRecorder;
import android.util.Log;
import java.io.File;
import java.io.IOException;

class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    /** Create a File for saving an image or audio file */
    static File getOutputMediaFile(){
        try {
            return File.createTempFile("tempfile", ".wav");
        } catch (IOException e) {
            Log.e(TAG,"Creating " + ".wav" + " file failed with exception : " + e.getMessage());
            return null;
        }
    }

    static void startMediaRecorder(MediaRecorder mediaRecorder, File audioFile) {
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mediaRecorder.setAudioSamplingRate(44100);
        mediaRecorder.setAudioChannels(1);
        mediaRecorder.setAudioEncodingBitRate(16000);
        mediaRecorder.setOutputFile(audioFile.getAbsolutePath());
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "MediaRecorder prepare failed");
        }
        mediaRecorder.start();
    }
}