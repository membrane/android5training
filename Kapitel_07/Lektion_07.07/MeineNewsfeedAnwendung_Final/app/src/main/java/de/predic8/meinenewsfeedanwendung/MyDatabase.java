package de.predic8.meinenewsfeedanwendung;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.List;

public class MyDatabase {

    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_TITLE = "title";
    }



    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_TITLE + " TEXT" +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;



    public class FeedReaderDbHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 5;
        public static final String DATABASE_NAME = "FeedReader.db";

        public FeedReaderDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            if (oldVersion == 1) {
                // TODO: database schema changes
                oldVersion = 4;
            }

            if (oldVersion == 4) {
                // TODO: database schema changes
                oldVersion = 5;
            }

            if (oldVersion == DATABASE_VERSION) {
                return;
            }


            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }




    private Context context;

    public MyDatabase(Context context) {
        this.context = context;
    }


    private static Object syncRoot = new Object();


    public void removeAll() {
        synchronized (syncRoot) {
            FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            try {
                db.delete(FeedEntry.TABLE_NAME, null, null);
            } finally {
                db.close();
            }
        }
    }

    public void insert(String title) {
        synchronized (syncRoot) {
            FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            try {
                ContentValues values = new ContentValues();
                values.put(FeedEntry.COLUMN_NAME_TITLE, title);

                long newRowId;
                newRowId = db.insert(FeedEntry.TABLE_NAME, FeedEntry.COLUMN_NAME_TITLE, values);
            } finally {
                db.close();
            }
        }
    }

    public List<String> getAll() {
        synchronized (syncRoot) {
            FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            try {
                ArrayList<String> result = new ArrayList<>();
                Cursor c = db.query(FeedEntry.TABLE_NAME, new String[]{FeedEntry.COLUMN_NAME_TITLE}, null, null, null, null, null);
                try {
                    while (c.moveToNext()) {
                        result.add(c.getString(0));
                    }
                    return result;
                } finally {
                    c.close();
                }
            } finally {
                db.close();
            }
        }
    }
}
