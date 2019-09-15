package com.example.live_chords;

import

import androidx.lifecycle.ViewModel;

public class AudioAnalysis extends ViewModel {

    AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,1024,0);

    PitchDetectionHandler pdh = new PitchDetectionHandler() {
        @Override
        public void handlePitch(PitchDetectionResult result,AudioEvent e) {
            final float pitchInHz = result.getPitch();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView text = (TextView) findViewById(R.id.textView1);
                    text.setText("" + pitchInHz);
                }
            });
        }
    };
    AudioProcessor p = new PitchProcessor(PitchEstimationAlgorithm.FFT_YIN, 22050, 1024, pdh);
dispatcher.addAudioProcessor(p);
new Thread(dispatcher,"Audio Dispatcher").start();
}
