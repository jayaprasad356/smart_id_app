package com.app.ai_di.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.app.ai_di.model.DemoCodeData;

import java.util.ArrayList;
import java.util.List;


public class SQLDatabaseHelper extends SQLiteOpenHelper {

    // Database and Table Info
    private static final String DATABASE_NAME = "DemoDatabase.db";
    private static final int DATABASE_VERSION = 1;  // Increase if schema changes
    private static final String TABLE_DEMO_DATA = "DemoData";

    // DemoData Table Columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_SCHOOL_NAME = "school_name";
    private static final String COLUMN_STUDENT_NAME = "student_name";
    private static final String COLUMN_ROLL_NUMBER = "roll_number";
    private static final String COLUMN_DOB = "dob";

    // SQL query to create the DemoData table
    private static final String CREATE_TABLE_DEMO_DATA = "CREATE TABLE IF NOT EXISTS " + TABLE_DEMO_DATA + " ("
            + COLUMN_ID + " TEXT PRIMARY KEY, "
            + COLUMN_SCHOOL_NAME + " TEXT, "
            + COLUMN_STUDENT_NAME + " TEXT, "
            + COLUMN_ROLL_NUMBER + " TEXT, "
            + COLUMN_DOB + " TEXT)";

    public SQLDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the DemoData table
        db.execSQL(CREATE_TABLE_DEMO_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if it exists, then recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEMO_DATA);
        onCreate(db);
    }

    // Method to insert data into DemoData table
    public boolean insertDemoData(DemoCodeData demoData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, demoData.getId());
        values.put(COLUMN_SCHOOL_NAME, demoData.getSchoolName());
        values.put(COLUMN_STUDENT_NAME, demoData.getStudentName());
        values.put(COLUMN_ROLL_NUMBER, demoData.getRollNumber());
        values.put(COLUMN_DOB, demoData.getDateOfBirth());

        long result = db.insert(TABLE_DEMO_DATA, null, values);
        db.close();

        return result != -1; // returns true if insert is successful, false otherwise
    }

    // Method to retrieve all data from DemoData table
    public List<DemoCodeData> getAllDemoData() {
        List<DemoCodeData> demoDataList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DEMO_DATA, null);

        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String schoolName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SCHOOL_NAME));
                String studentName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STUDENT_NAME));
                String rollNumber = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROLL_NUMBER));
                String dob = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOB));

                DemoCodeData data = new DemoCodeData(id, schoolName, studentName, rollNumber, dob);
                demoDataList.add(data);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return demoDataList;
    }
}

