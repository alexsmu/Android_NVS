package byuie499.auto_nvs;

import android.os.Handler;
import android.os.Message;

public class Fft {
    private double[] cosTable;
    private double[] sinTable;
    private int N;
    public double[] data;
    private double[] real;
    private double[] imag;
    private double[] mag;
    private double[] shifted;
    private int levels;
    private Handler mHandler;
    private int what;

    public Fft(int samples, Handler global_handler, int code){
        N = samples;
        mHandler = global_handler;
        what = code;
        cosTable = new double[N / 2];
        sinTable = new double[N / 2];
        real = new double[N];
        imag = new double[N];
        mag = new double[N];
        shifted = new double[N];
        levels = 31 - Integer.numberOfLeadingZeros(N);  // Equal to floor(log2(n))
        for (int i = 0; i < N / 2 ; ++i) {
            cosTable[i] = Math.cos(2 * Math.PI * i / N);
            sinTable[i] = Math.sin(2 * Math.PI * i / N);
        }
    }

    public static void getOmega(double[] omega, double Fs) {
        for (int i = 0; i < omega.length; ++i) {
            omega[i] = (i - omega.length / 2) * Fs / omega.length;
        }
    }

    private void prepare() {
        for (int i = 0; i < data.length; ++i) {
            real[i] = data[i];
            imag[i] = 0;
        }
    }

    private void getMagnitudeDB(){
        for (int i = 0; i < N; ++i){
            mag[i] = 20 * Math.log10(Math.hypot(real[i], imag[i]) / N);
        }
    }

    private void shift() {
        for (int i = 0; i < N; ++i)
            shifted[i] = mag[(N / 2 + i) % N];
    }

    public void run(double[] samples) {
        data = samples;
        Thread fftThread = new Thread(new Runnable() {
            @Override
            public void run() {
                prepare();
                transform();
                getMagnitudeDB();
                shift();
                Message done = mHandler.obtainMessage(what, shifted);
                mHandler.sendMessage(done);
            }
        }, "auto_nvs_fft");
        fftThread.start();
    }

    /*
     * Free FFT and convolution (Java)
     *
     * Copyright (c) 2014 Project Nayuki
     * https://www.nayuki.io/page/free-small-fft-in-multiple-languages
     *
     * (MIT License)
     * Permission is hereby granted, free of charge, to any person obtaining a copy of
     * this software and associated documentation files (the "Software"), to deal in
     * the Software without restriction, including without limitation the rights to
     * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
     * the Software, and to permit persons to whom the Software is furnished to do so,
     * subject to the following conditions:
     * - The above copyright notice and this permission notice shall be included in
     *   all copies or substantial portions of the Software.
     * - The Software is provided "as is", without warranty of any kind, express or
     *   implied, including but not limited to the warranties of merchantability,
     *   fitness for a particular purpose and noninfringement. In no event shall the
     *   authors or copyright holders be liable for any claim, damages or other
     *   liability, whether in an action of contract, tort or otherwise, arising from,
     *   out of or in connection with the Software or the use or other dealings in the
     *   Software.
     */
    private void transform() {
        // Bit-reversed addressing permutation
        for (int i = 0; i < N; i++) {
            int j = Integer.reverse(i) >>> (32 - levels);
            if (j > i) {
                double temp = real[i];
                real[i] = real[j];
                real[j] = temp;
                temp = imag[i];
                imag[i] = imag[j];
                imag[j] = temp;
            }
        }

        // Cooley-Tukey decimation-in-time radix-2 FFT
        for (int size = 2; size <= N; size *= 2) {
            int halfsize = size / 2;
            int tablestep = N / size;
            for (int i = 0; i < N; i += size) {
                for (int j = i, k = 0; j < i + halfsize; j++, k += tablestep) {
                    double tpre =  real[j+halfsize] * cosTable[k] + imag[j+halfsize] * sinTable[k];
                    double tpim = -real[j+halfsize] * sinTable[k] + imag[j+halfsize] * cosTable[k];
                    real[j + halfsize] = real[j] - tpre;
                    imag[j + halfsize] = imag[j] - tpim;
                    real[j] += tpre;
                    imag[j] += tpim;
                }
            }
            if (size == N)  // Prevent overflow in 'size *= 2'
                break;
        }
    }
}


