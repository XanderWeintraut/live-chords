package com.example.live_chords;

/*
 *
 * When button is pressed, begins recording audio and processes it.
 *
 * Based heavily off of example found here: https://developer.android.com/guide/topics/media/mediarecorder#java
 * Also based heavily off of: https://www.tutorialspoint.com/android/android_audio_capture.htm
 *
 * @Author Xander Weintraut and Anirudh Rao
 * @Version 2:04 AM, September 15 2019
 *
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SpectralPeakProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.FFTPitch;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.util.PitchConverter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class SecondActivity extends AppCompatActivity {
    private int sampleRate = 44100;
    private int bufferSize = 1024 * 4;
    private int overlap = 768 * 4 ;
    private int notesInChord = 3;
    private float noiseFloorFactor = 0.1f;

    ImageButton buttonStart;
    public static final int RequestPermissionCode = 1;
    boolean active = false;

    public SpectralPeakProcessor peaks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        peaks = new SpectralPeakProcessor(bufferSize, overlap, sampleRate)
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
        dispatcher.addAudioProcessor(peaks);

        buttonStart = findViewById(R.id.imageButton3);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()) {
                    if(!active){
                        setContentView(R.layout.activity_three);
                        new Thread(dispatcher,"Audio Dispatcher").start();

                    }
                    else{
                        setContentView(R.layout.activity_second);
                    }
                } else {
                    requestPermission();
                }
            }
        });
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(SecondActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public  void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch(requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(SecondActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(SecondActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public String getChord() {
        String chordName;
        double[] peakFrequencies = getPeakFrequencies();
        int[] noteValues = new int[peakFrequencies.length];

        for(int i = 0; i < peakFrequencies.length; i++) {
            noteValues[i] = PitchConverter.hertzToMidiKey(peakFrequencies[i]);
        }



        return chordName;
    }

    private double[] getPeakFrequencies() {
        float[] magnitudes = peaks.getMagnitudes();
        float[] freqEstimates = peaks.getFrequencyEstimates();
        int median = (int) Math.round(SpectralPeakProcessor.median(magnitudes));

        float[] noiseFloor = peaks.calculateNoiseFloor(magnitudes, median, noiseFloorFactor);
        List<Integer> localMaxima = peaks.findLocalMaxima(magnitudes, noiseFloor);

        List<SpectralPeakProcessor.SpectralPeak> spectralPeaks = peaks.findPeaks(magnitudes, freqEstimates, localMaxima, notesInChord, 14);

        double[] peakFrequencies = new double[notesInChord];
        for(int i = 0; i < notesInChord; i++) {
            peakFrequencies[i] = spectralPeaks.get(i).getFrequencyInHertz();
        }
        return peakFrequencies;
    }
}
