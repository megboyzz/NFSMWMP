package com.eamobile.download;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class SpeedCalculator {
    private float averageSpeed = BitmapDescriptorFactory.HUE_RED;
    private float currentAmount = BitmapDescriptorFactory.HUE_RED;
    private float currentSpeed = BitmapDescriptorFactory.HUE_RED;
    private float currentTime = BitmapDescriptorFactory.HUE_RED;
    private float previousAmount = BitmapDescriptorFactory.HUE_RED;
    private float previousTime = BitmapDescriptorFactory.HUE_RED;
    private float smoothingFactor = BitmapDescriptorFactory.HUE_RED;

    public SpeedCalculator(float f) {
        this.smoothingFactor = f;
    }

    public void forceAmount(float f) {
        this.previousAmount = f;
    }

    public float getCurrentSpeed() {
        return this.averageSpeed;
    }

    public void reportAmount(float f, float f2) {
        this.currentAmount = f;
        this.currentTime = f2;
        float f3 = this.currentAmount - this.previousAmount;
        float f4 = this.currentTime - this.previousTime;
        this.previousAmount = this.currentAmount;
        this.previousTime = this.currentTime;
        float f5 = (this.smoothingFactor * this.currentSpeed) + ((1.0f - this.smoothingFactor) * this.averageSpeed);
        if (!Float.isNaN(f5) && !Float.isInfinite(f5)) {
            this.averageSpeed = f5;
        }
        if (f4 > 0.001f) {
            this.currentSpeed = f3 / f4;
        }
    }
}
