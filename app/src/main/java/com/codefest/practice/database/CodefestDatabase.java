package com.codefest.practice.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Product.class}, version = 2)
public abstract class CodefestDatabase extends RoomDatabase {
    public static CodefestDatabase instance;

    public static CodefestDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.
                    databaseBuilder(context, CodefestDatabase.class, "codefest_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract ProductDao productDao();
}
