package com.facebook.android;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Skeleton base class for RequestListeners, providing default error 
 * handling. Applications should handle these error conditions.
 *
 */
public abstract class BaseRequestListener implements AsyncFacebookRunner.RequestListener {

    public void onFacebookError(FacebookError e) {
        Log.e("Facebook", e.getMessage());
        e.printStackTrace();
    }

    public void onFileNotFoundException(FileNotFoundException e) {
        Log.e("Facebook", e.getMessage());
        e.printStackTrace();
    }

    public void onIOException(IOException e) {
        Log.e("Facebook", e.getMessage());
        e.printStackTrace();
    }

    public void onMalformedURLException(MalformedURLException e) {
        Log.e("Facebook", e.getMessage());
        e.printStackTrace();
    }
    
}
