package com.example.networkingexamples2;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class AppExecutor {

    private static com.example.networkingexamples2.AppExecutor instance;
    private final Executor networkExecutor;
    private final Executor diskIoExecutor;
    private final Executor mainExecutor;

    public static com.example.networkingexamples2.AppExecutor getInstance(){
        if (instance == null){
            instance = new com.example.networkingexamples2.AppExecutor();
        }
        return instance;
    }
     private AppExecutor(){
        networkExecutor = Executors.newFixedThreadPool(3);
        diskIoExecutor = Executors.newSingleThreadExecutor();
        mainExecutor = new MainThreadExecutor();
     }

    public Executor getNetworkExecutor() {
        return networkExecutor;
    }

    public Executor getDiskIoExecutor() {
        return diskIoExecutor;
    }

    public Executor getMainExecutor() {
        return mainExecutor;
    }

    private static class MainThreadExecutor implements Executor {

        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}