package com.blogspot.e_kanivets.moneytracker.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.blogspot.e_kanivets.moneytracker.model.Category;
import com.blogspot.e_kanivets.moneytracker.model.Record;
import com.blogspot.e_kanivets.moneytracker.util.Constants;
import com.blogspot.e_kanivets.moneytracker.util.MTApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by eugene on 01/09/14.
 */
public class MTHelper extends Observable {

    private static MTHelper mtHelper;

    private DBHelper dbHelper;

    private List<Category> categories;
    private List<Record> records;

    public static MTHelper getInstance() {
        if(mtHelper == null) {
            mtHelper = new MTHelper();
        }
        return mtHelper;
    }

    private MTHelper() {
        dbHelper = new DBHelper(MTApp.get());
    }

    public void initialize() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Read categories table from db
        Cursor cursor = db.query(Constants.TABLE_CATEGORIES, null, null, null, null, null, null);
        categories = new ArrayList<Category>();

        if(cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id");
            int nameColIndex = cursor.getColumnIndex("name");

            do {
                //Read a record from DB
                Category category = new Category(cursor.getInt(idColIndex),
                        cursor.getString(nameColIndex));

                //Add record to list
                categories.add(category);
            } while (cursor.moveToNext());
        }

        cursor.close();

        //Read records table from db
        cursor = db.query(Constants.TABLE_RECORDS, null, null, null, null, null, null);
        records = new ArrayList<Record>();

        if(cursor.moveToFirst()) {
            //Get indexes of columns
            int idColIndex = cursor.getColumnIndex("id");
            int timeColIndex = cursor.getColumnIndex("time");
            int typeColIndex = cursor.getColumnIndex("type");
            int titleColIndex = cursor.getColumnIndex("title");
            int categoryColIndex = cursor.getColumnIndex("category_id");
            int priceColIndex = cursor.getColumnIndex("price");

            do {
                //Read a record from DB
                Record record = new Record(cursor.getInt(idColIndex),
                        cursor.getInt(timeColIndex),
                        cursor.getInt(typeColIndex),
                        cursor.getString(titleColIndex),
                        cursor.getInt(categoryColIndex),
                        cursor.getInt(priceColIndex));

                //Add record to list
                records.add(record);
            } while (cursor.moveToNext());
        }

        db.close();
    }

    public List<Record> getRecords() {
        return records;
    }

    public void addRecord(int time, int type, String title, String category, int price) {
        //Add record to DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("type", "1");
        contentValues.put("title", title);
        contentValues.put("category_id", 1);
        contentValues.put("price", price);

        int id = (int) db.insert(Constants.TABLE_RECORDS, null, contentValues);

        db.close();

        if(getCategoryIdByName(category) == -1) {
            addCategory(category);
        }
        int category_id = getCategoryIdByName(category);

        //Add record to app list
        records.add(new Record(id, time, type, title, category_id, price));

        //notify observers
        setChanged();
        notifyObservers();
    }

    public void deleteRecordById(int id) {
        for (Record record : records) {
            if(record.getId() == id) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete(Constants.TABLE_RECORDS, "id=?",
                        new String[] {Integer.toString(id)});
                db.close();

                records.remove(record);

                //notify observers
                setChanged();
                notifyObservers();

                break;
            }
        }
    }

    public int addCategory(String name) {
        //Add category to DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);

        int id = (int) db.insert(Constants.TABLE_CATEGORIES, null, contentValues);

        db.close();

        //Add category to app list
        categories.add(new Category(id, name));

        return id;
    }

    public void deleteCategoryById(int id) {
        for(Category category : categories) {
            if(category.getId() == id) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete(Constants.TABLE_CATEGORIES, "id=?",
                        new String[] {Integer.toString(id)});

                categories.remove(category);

                break;
            }
        }
    }

    public String getCategoryById(int id) {
        for (Category category : categories) {
            if(category.getId() == id) return category.getName();
        }

        return null;
    }

    public int getCategoryIdByName(String name) {
        for(Category category : categories) {
            if(category.getName().equals(name)) {
                return category.getId();
            }
        }

        return -1;
    }
}
