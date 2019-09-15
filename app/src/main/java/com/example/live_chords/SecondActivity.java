package com.example.live_chords;

/*
 *
 * When button is pressed, begins recording audio and processes it.
 *
 * Based heavily off of example found here: https://developer.android.com/guide/topics/media/mediarecorder#java
 * Also based heavily off of: https://www.tutorialspoint.com/android/android_audio_capture.htm
 *
 * @Author Xander Weintraut, Anirudh Rao, Charlie Horn
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
import androidx.core.view.DragStartHelper;

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
import static android.util.Log.d;

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

    Thread thread = new Thread() {
        @Override
        public void run() {
            try {
                while(true) {
                    sleep(200);
                    handler.post(this);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        peaks = new SpectralPeakProcessor(bufferSize, overlap, sampleRate);
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);
        dispatcher.addAudioProcessor(peaks);

        buttonStart = findViewById(R.id.imageButton3);

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPermission()) {
                    if(!active){
                        setContentView(R.layout.activity_three);
                        thread.start();

                    }
                    else{
                        setContentView(R.layout.activity_second);
                        thread.stop();
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

        double[] peakFrequencies = getPeakFrequencies();
        int[] noteValues = new int[peakFrequencies.length];

        for(int i = 0; i < peakFrequencies.length; i++) {
            noteValues[i] = PitchConverter.hertzToMidiKey(peakFrequencies[i]);
        }

        boolean cNote = false;
        boolean cSHNote = false;
        boolean dNote = false;
        boolean dSHNote = false;
        boolean eNote = false;
        boolean fNote = false;
        boolean fSHNote = false;
        boolean gNote = false;
        boolean gSHNote = false;
        boolean aNote = false;
        boolean aSHNote = false;
        boolean bNote = false;

        for (int k = 0; k<noteValues.length; k++){ //loop through each value in the array
            switch (noteValues[k]%12){ //determine the remainder of the MIDI value/12 and assign note name
                case 0: cNote = true;
                break;
                case 1: cSHNote = true;
                break;
                case 2: dNote = true;
                break;
                case 3: dSHNote = true;
                break;
                case 4: eNote = true;
                break;
                case 5: fNote = true;
                break;
                case 6: fSHNote = true;
                break;
                case 7: gNote = true;
                break;
                case 8: gSHNote = true;
                break;
                case 9: aNote = true;
                break;
                case 10: aSHNote = true;
                break;
                case 11: bNote = true;
                break;
                default: break;
            }
        } //end determining present notes

        /////////////DEFINE MAJOR CHORDS/////////////
        if (cNote && eNote && gNote){
            return "C MAJOR";
        }
        if (cSHNote && fNote && gSHNote){
            return "C SHARP MAJOR";
        }
        if (dNote && fSHNote && aNote){
            return "D MAJOR";
        }
        if (dSHNote && gNote && aSHNote){
            return "D SHARP MAJOR";
        }
        if (eNote && gSHNote && bNote){
            return "E MAJOR";
        }
        if (fNote && aNote && cNote){
            return "F MAJOR";
        }
        if (fSHNote && aSHNote && cSHNote){
            return "F SHARP MAJOR";
        }
        if (gNote && bNote && dNote){
            return "G MAJOR";
        }
        if (gSHNote && cNote && dSHNote){
            return "G SHARP MAJOR";
        }
        if (aNote && cSHNote && eNote){
            return "A MAJOR";
        }
        if (aSHNote && dNote && fNote){
            return "A SHARP MAJOR";
        }
        if (bNote && dSHNote && fSHNote){
            return "B MAJOR";
        }

        //////////END MAJOR//////////
        //////////BEGIN MINOR////////

        if (cNote && dSHNote && gNote){
            return "C MINOR";
        }
        if (cSHNote && eNote && gSHNote){
            return "C SHARP MINOR";
        }
        if (dNote && fNote && aNote){
            return "D MINOR";
        }
        if (dSHNote && fSHNote && aSHNote){
            return "D SHARP MINOR";
        }
        if (eNote && gNote && bNote){
            return "E MINOR";
        }
        if (fNote && gSHNote && cNote){
            return "F MINOR";
        }
        if (fSHNote && aNote && cSHNote){
            return "F SHARP MINOR";
        }
        if (gNote && aSHNote && dNote){
            return "G MINOR";
        }
        if (gSHNote && bNote && dSHNote){
            return "G SHARP MINOR";
        }
        if (aNote && cNote && eNote){
            return "A MINOR";
        }
        if (aSHNote && cSHNote && fNote){
            return "A SHARP MINOR";
        }
        if (bNote && dNote && fSHNote){
            return "B MINOR";
        }
        return "Chord not recognized";
    }
    ////END CHORD IDENTIFIER////

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
