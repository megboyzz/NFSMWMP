package com.ea.ironmonkey;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class Accelerometer implements SensorEventListener {
    private int bufferReadIndex;
    private int bufferSize;
    private int[] bufferTimesteps;
    private float[] bufferValues;
    private int bufferWriteIndex;
    private long lastTimestamp = 0;
    private int naturalOrientation;
    private boolean registered;
    private float samplesPerSecond;
    private Sensor sensor;
    private SensorManager sensorManager;

    public Accelerometer(SensorManager sensorManager2, Sensor sensor2, int i) {
        this.sensorManager = sensorManager2;
        this.sensor = sensor2;
        this.naturalOrientation = i;
    }

    private int getSensorDelay() {
        return this.samplesPerSecond < 20.0f ? 3 : 1;
    }

    private void register() {
        if (!this.registered && this.samplesPerSecond > BitmapDescriptorFactory.HUE_RED) {
            this.sensorManager.registerListener(this, this.sensor, getSensorDelay());
            this.registered = true;
        } else if (this.registered && this.samplesPerSecond == BitmapDescriptorFactory.HUE_RED) {
            this.sensorManager.unregisterListener(this);
            this.registered = false;
        }
    }

    private void unregister() {
        if (this.registered) {
            this.sensorManager.unregisterListener(this);
            this.registered = false;
        }
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public float getFrequency() {
        return this.samplesPerSecond;
    }

    public int getSamples(int i, int[] iArr, float[] fArr) {
        int i2 = 0;
        synchronized (this) {
            while (this.bufferReadIndex != this.bufferWriteIndex) {
                if (this.bufferReadIndex >= this.bufferSize) {
                    this.bufferReadIndex = 0;
                }
                if (i2 >= i) {
                    break;
                }
                iArr[i2] = this.bufferTimesteps[this.bufferReadIndex];
                for (int i3 = 0; i3 < 3; i3++) {
                    fArr[(i2 * 3) + i3] = this.bufferValues[(this.bufferReadIndex * 3) + i3];
                }
                i2++;
                this.bufferReadIndex++;
            }
        }
        return i2;
    }

    public void onAccuracyChanged(Sensor sensor2, int i) {
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        int i = (int) ((sensorEvent.timestamp - this.lastTimestamp) / 1000000);
        this.lastTimestamp = sensorEvent.timestamp;
        synchronized (this) {
            this.bufferWriteIndex++;
            if (this.bufferWriteIndex >= this.bufferSize) {
                this.bufferWriteIndex = 0;
            }
            if (this.bufferWriteIndex == this.bufferReadIndex) {
                this.bufferReadIndex++;
            }
            this.bufferTimesteps[this.bufferWriteIndex] = i;
            switch (this.naturalOrientation) {
                case 0:
                    this.bufferValues[(this.bufferWriteIndex * 3) + 0] = -sensorEvent.values[0];
                    this.bufferValues[(this.bufferWriteIndex * 3) + 1] = sensorEvent.values[1];
                    break;
                case 1:
                    this.bufferValues[(this.bufferWriteIndex * 3) + 0] = sensorEvent.values[1];
                    this.bufferValues[(this.bufferWriteIndex * 3) + 1] = -sensorEvent.values[0];
                    break;
                case 2:
                    this.bufferValues[(this.bufferWriteIndex * 3) + 0] = sensorEvent.values[0];
                    this.bufferValues[(this.bufferWriteIndex * 3) + 1] = -sensorEvent.values[1];
                    break;
                case 3:
                    this.bufferValues[(this.bufferWriteIndex * 3) + 0] = -sensorEvent.values[1];
                    this.bufferValues[(this.bufferWriteIndex * 3) + 1] = sensorEvent.values[0];
                    break;
            }
            this.bufferValues[(this.bufferWriteIndex * 3) + 2] = sensorEvent.values[2];
        }
    }

    public void pause() {
        unregister();
    }

    public void resume() {
        register();
    }

    public void setBufferSize(int i) {
        this.bufferSize = i;
        this.bufferTimesteps = new int[i];
        this.bufferValues = new float[(i * 3)];
        this.bufferWriteIndex = 0;
        this.bufferReadIndex = 0;
    }

    public void setFrequency(float f) {
        this.samplesPerSecond = f;
        register();
    }
}
