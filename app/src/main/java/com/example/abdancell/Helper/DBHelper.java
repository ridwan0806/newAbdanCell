package com.example.abdancell.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.abdancell.Model.OrderItem;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Order";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_NAME = "OrderItem";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PRODUCT_ID = "productId";
    private static final String COLUMN_PRODUCT_NAME = "productName";
    private static final String COLUMN_PRODUCT_CATEGORY = "productCategory";
    private static final String COLUMN_QUANTITY = "qty";
    private static final String COLUMN_HPP = "hargaModal";
    private static final String COLUMN_PRICE = "hargaJual";
    private static final String COLUMN_SUBTOTAL_MODAL = "subtotalModal";
    private static final String COLUMN_SUBTOTAL_MARGIN = "subtotalMargin";
    private static final String COLUMN_SUBTOTAL_SALES = "subtotalSales";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_ID + " TEXT, " +
                COLUMN_PRODUCT_NAME + " TEXT, " +
                COLUMN_PRODUCT_CATEGORY + " TEXT, " +
                COLUMN_QUANTITY + " TEXT, " +
                COLUMN_HPP + " TEXT, " +
                COLUMN_PRICE + " TEXT, " +
                COLUMN_SUBTOTAL_MODAL + " TEXT, " +
                COLUMN_SUBTOTAL_MARGIN + " TEXT, " +
                COLUMN_SUBTOTAL_SALES + " TEXT);";
        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public int checkItemExist(String row_id) {
        int check = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT productId FROM OrderItem WHERE productId = ?",new String[] {row_id});
        if (result.moveToFirst()) {
            return check;
        }
        else {
            check = 1;
        }
        return check;
    }

    public void addToCart(OrderItem orderItem)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

//        cv.put(COLUMN_ID, orderItem.getId());
        cv.put(COLUMN_PRODUCT_ID, orderItem.getProductId());
        cv.put(COLUMN_PRODUCT_NAME, orderItem.getProductName());
        cv.put(COLUMN_PRODUCT_CATEGORY, orderItem.getProductKategori());
        cv.put(COLUMN_QUANTITY, orderItem.getQty());
        cv.put(COLUMN_HPP, orderItem.getHargaModal());
        cv.put(COLUMN_PRICE, orderItem.getHargaJual());
        cv.put(COLUMN_SUBTOTAL_MODAL, orderItem.getSubtotalModal());
        cv.put(COLUMN_SUBTOTAL_MARGIN, orderItem.getSubtotalMargin());
        cv.put(COLUMN_SUBTOTAL_SALES, orderItem.getSubtotalSales());

        db.insert(TABLE_NAME,null,cv);
    }
}
