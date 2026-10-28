
package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "CountryDB.db";
    private static final String TABLE = "countries";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE countries (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "country TEXT NOT NULL, " +
                "capital TEXT NOT NULL, " +
                "currency TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // No migration needed for this basic project.
    }

    // CREATE
    public boolean insertCountry(String country,
                                 String capital,
                                 String currency) {
        ContentValues values = new ContentValues();
        values.put("country", country);
        values.put("capital", capital);
        values.put("currency", currency);

        return getWritableDatabase().insert(
                TABLE, null, values) != -1;
    }

    // READ ALL
    public Cursor getAllCountries() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM countries ORDER BY country",
                null);
    }

    // READ ONE
    public Cursor getCountryById(int id) {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM countries WHERE id = ?",
                new String[]{String.valueOf(id)});
    }

    // UPDATE
    public boolean updateCountry(int id, String country,
                                 String capital, String currency) {
        ContentValues values = new ContentValues();
        values.put("country", country);
        values.put("capital", capital);
        values.put("currency", currency);

        return getWritableDatabase().update(
                TABLE, values, "id = ?",
                new String[]{String.valueOf(id)}) > 0;
    }

    // DELETE
    public boolean deleteCountry(int id) {
        return getWritableDatabase().delete(
                TABLE, "id = ?",
                new String[]{String.valueOf(id)}) > 0;
    }
}
