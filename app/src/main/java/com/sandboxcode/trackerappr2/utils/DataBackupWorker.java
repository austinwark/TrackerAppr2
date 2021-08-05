package com.sandboxcode.trackerappr2.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.sandboxcode.trackerappr2.repositories.SearchRepository;

public class DataBackupWorker extends Worker {

    private static final String TAG = DataBackupWorker.class.getSimpleName();

    public DataBackupWorker(
            @NonNull Context context,
            @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {

        Context applicationContext = getApplicationContext();

        Log.d(TAG, "--------------------- DOING WORK ---------------------");

        try {
            SearchRepository repository = new SearchRepository((Application) applicationContext);
            repository.backupDataToFirebase();
            return Result.success();

        } catch (Exception e) {
            return Result.failure();
        }
    }
}
