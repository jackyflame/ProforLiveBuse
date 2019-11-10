package com.jf.livedatabus;

import android.util.Log;

public class LogW {

    public static String TAG = "JF.LOG";
    public static boolean isDebug = true;

    public static void i(String msg){
        if(isDebug){
            Log.i(TAG,msg);
        }
    }

    public static void i(String title, String msg){
        if(isDebug) {
            Log.i(TAG, title + " >>> " + msg);
        }
    }

    public static void d(String msg){
        if(isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String title, String msg){
        if(isDebug) {
            Log.d(TAG, title + " >>> " + msg);
        }
    }

    public static void e(String msg){
        if(isDebug) {
            Log.e(TAG, msg);
        }
    }

    public static void e(String title, String msg){
        if(isDebug) {
            Log.e(TAG, title + " >>> " + msg);
        }
    }

}
