package hanu.a2_1901040128.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import hanu.a2_1901040128.models.Products;

public class DatabaseHandler extends SQLiteOpenHelper {
    private Context context;
    private static final String DBNAME = "productsCart.db";
    private static final int VERSION = 1;
    private static final String TABLE_NAME = "myCart";
    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String THUMBNAIL = "thumbnail";
    private static final String UNITPRICE = "unitPrice";
    private static final String QUANTITY = "quantity";
    private static final String SUMPRICE = "sumPrice";
    private SQLiteDatabase db;



    public DatabaseHandler(@Nullable Context context) {
        super(context, DBNAME, null, VERSION);
    }

    public static String getID() {
        return ID;
    }

    public static String getNAME() {
        return NAME;
    }

    public static String getTHUMBNAIL() {
        return THUMBNAIL;
    }

    public static String getUNITPRICE() {
        return UNITPRICE;
    }

    public static String getQUANTITY() {
        return QUANTITY;
    }

    public static String getSUMPRICE() {
        return SUMPRICE;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " ( " +
                ID + " INTEGER," +
                THUMBNAIL + " VARCHAR NOT NULL," +
                NAME + " VARCHAR(100) NOT NULL, " +
                UNITPRICE + " INTEGER NOT NULL," +
                QUANTITY + " INTEGER NOT NULL," +
                SUMPRICE + " INTEGER NOT NULL" + ")";

        sqLiteDatabase.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int ii) {
        db.execSQL("drop Table if exists " + TABLE_NAME);
    }

    public long Insert(Products products) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID, products.getID());
        values.put(THUMBNAIL, products.getThumbnail());
        values.put(NAME, products.getName());
        values.put(UNITPRICE, products.getUnitPrice());
        values.put(QUANTITY, products.getQuantity());
        values.put(SUMPRICE, products.getSumPrice());

        return db.insert(TABLE_NAME, null, values);
    }

    public void Update(Products products) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(QUANTITY, products.getQuantity());
        values.put(SUMPRICE, products.getSumPrice());
        int row = db.update(TABLE_NAME, values, ID + "=?", new String[]{products.getID() + ""});
        db.close();
    }

    public void Delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long rs = db.delete(TABLE_NAME, ID + "=?", new String[]{id + ""});
        db.close();
    }

    public Cursor getAll() {
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return cursor;
    }

    public Products getProductByID(int ID) {
        Products products = null;
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE id=?", new String[]{ID + ""});
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            int productID = cursor.getInt(0);
            String productThumb = cursor.getString(1);
            String productName = cursor.getString(2);
            int productUnitPrice = cursor.getInt(3);
            int productQuantity = cursor.getInt(4);
            int productSumPrice = cursor.getInt(5);

            products = new Products(productID, productThumb, productName, productUnitPrice, productQuantity, productSumPrice);
        }
        return products;
    }

    public void openDB() {
        db = getWritableDatabase();
    }

    public void closeDB() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}

