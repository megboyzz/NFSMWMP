package com.ea.ironmonkey.devmenu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ea.nimble.ApplicationLifecycle;

//Активность-марионетка для проверки работы нативных методов жизненного цикла
public class PuppetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApplicationLifecycle.onActivityCreate(savedInstanceState, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApplicationLifecycle.onActivityResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ApplicationLifecycle.onActivityStart(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationLifecycle.onActivityDestroy(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ApplicationLifecycle.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ApplicationLifecycle.onActivityResult(resultCode, requestCode, data, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationLifecycle.onActivityPause(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        ApplicationLifecycle.onActivityRestart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ApplicationLifecycle.onActivityStop(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ApplicationLifecycle.onActivityRestoreInstanceState(savedInstanceState, this);
    }

}
