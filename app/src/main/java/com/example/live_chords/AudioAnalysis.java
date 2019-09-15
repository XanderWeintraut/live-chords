package com.example.live_chords;

import android.widget.TextView;

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


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AudioAnalysis<p> extends ViewModel implements Runnable {
    private final MutableLiveData<String> chordLiveData = new MutableLiveData<>();
    private final MutableLiveData<Tempo> bpmLiveData = new MutableLiveData<>();
    private AudioDispatcher dispatcher;

    private float sampleRate = 44100;
    private int bufferSize = 1024 * 4;
    private int overlap = 768 * 4 ;

    public AudioAnalysis() {
        dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

    }



    public LiveData<String> getChordName() {
        //To implement

    }

    private void setChordName() {
        //toImplement
    }

    public LiveData<Tempo> getTempo() {
        //To implement

    }

    private void setChordName() {
        //To implement
    }
}
