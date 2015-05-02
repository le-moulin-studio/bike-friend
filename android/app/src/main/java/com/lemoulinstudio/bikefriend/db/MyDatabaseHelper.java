package com.lemoulinstudio.bikefriend.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class MyDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "bikefriend.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        //Log.i(MyDatabaseHelper.class.getName(), "onCreate()");

        try {
            // Create the tables.
            TableUtils.createTable(connectionSource, BikeStation.class);
        }
        catch (SQLException e) {
            Log.e(MyDatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    }

}
