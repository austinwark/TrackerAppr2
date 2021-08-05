package com.sandboxcode.trackerappr2.room_components;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.sandboxcode.trackerappr2.models.ResultModel;
import com.sandboxcode.trackerappr2.models.SearchModel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {SearchModel.class, ResultModel.class}, version = 3)
public abstract class SearchRoomDatabase extends RoomDatabase {

    public abstract SearchDao getSearchDao();
    public abstract ResultDao getResultDao();

    private static volatile SearchRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static SearchRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (SearchRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SearchRoomDatabase.class, "search_database")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
