package com.ea.ironmonkey.devmenu.util;

import android.util.Log;

public class Observer {

    private static final String LOG_TAG = "Observer";

    public static void onCallingMethod(Method... states){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        Log.i(LOG_TAG, "info{");
        if(states.length != 0){
            Log.i(LOG_TAG, "States of method:");
            for(Method mthd : states){
                Log.i(LOG_TAG, "\t" + mthd.title);
            }
            Log.i(LOG_TAG, "\n");
        }
        for(int i = 1; i < stackTrace.length; i++) {
            Log.i(LOG_TAG, "\t" + stackTrace[i]);
        }
        Log.i(LOG_TAG, "}");
    }

    private interface MethodCallingCounter{


        void call();

    }

    /** Перечисление состояний методов при из анализе и изменении, доработке */
    public enum Method implements MethodCallingCounter{
        /** Состояние невозможгости декомпиляции */
        IMPOSSIBLE_TO_DECOMPILE("Impossible to decompile"){
            @Override
            public void call() {

            }
        },

        /** Уровни подозртельности работы методов при их воостановлении после
        декомпиляции, или их доработке и изменеии.

        /** Зеленая зона. Небольшие подозрения */
        SUSPICIOUS_METHOD("Suspicious method"){
            @Override
            public void call() {

            }
        },

        /** Желтая зона. уже более подозрительны метод, что ставит под вопрос корректоность отрработки некторого функционала */
        VERY_SUSPICIOUS_METHOD("Very suspicious Method") {
            @Override
            public void call() {

            }
        },

        /** Красная зона. Опасный метод который может привести к фатальным ошибкам */
        HAZARD_METHOD("Hazard method") {
            @Override
            public void call() {

            }
        },


        /** Уровни воостонавливаемости кода */
        HARD_TO_RECOVER_LOGIC("Hard to recover logic of method") {
            @Override
            public void call() {

            }
        },

        ON_CATCHING_EXCEPTION("on catching exception") {
            @Override
            public void call() {

            }
        },

        RETURN_NULL("Method returns null") {
            @Override
            public void call() {

            }
        },

        SOME_PACKAGE_IS_DELETED("Some package is deleted"){
            @Override
            public void call() {

            }
        };

        private String title;

        Method(String title) {
            this.title = title;
            //this.call();
        }

    }

}

