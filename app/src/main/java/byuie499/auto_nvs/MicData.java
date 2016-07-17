package byuie499.auto_nvs;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;

public class MicData {
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static AudioRecord recorder = null;
    private int buffer;
    private int bufferSizeInBytes;
    private static Thread recordingThread = null;
    public static boolean isRecording = false;
    public static boolean isEnabled = false;
    public Handler mHandler = null;
    private Fft audioFFT = null;
    public static double scale = 1.0;

    public MicData(Handler global_handler, int samples) {
        buffer = samples;
        bufferSizeInBytes = buffer * 2;
        mHandler = global_handler;
        audioFFT = new Fft(samples, mHandler, 0);
    }

    //Conversion from short to double
    private double[] short2double(short [] audioData){
        double[] micBufferData = new double[buffer];//size may need to change
        for (int i = 0; i < buffer; ++i)
            micBufferData[i] = scale * audioData[i] / buffer;
        return micBufferData;
    }

    private Runnable record = new Runnable() {
        @Override
        public synchronized void run() {
            short[] buff = new short[buffer];
            double[] mic_data;
            while (isRecording) {
                recorder.read(buff, 0, buffer);
                mic_data = short2double(buff);
                audioFFT.data = mic_data;
                audioFFT.prepare();
                audioFFT.transform();
                if (Fft.db)
                    audioFFT.getMagnitudeDB();
                else
                    audioFFT.getMagnitude();
                audioFFT.shift();
                Message done = mHandler.obtainMessage(0, audioFFT.shifted);
                mHandler.sendMessage(done);
            }
        }
    };

    public void run() {
        if (isEnabled & !isRecording) {
            isRecording = true;
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE,
                    RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSizeInBytes);
            recorder.startRecording();
            recordingThread = new Thread(record, "auto_nvs_recording");
            recordingThread.start();
        }
    }

    public void onPause() {
        isRecording = false;
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            try {
                recordingThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        recordingThread = null;
    }
}
