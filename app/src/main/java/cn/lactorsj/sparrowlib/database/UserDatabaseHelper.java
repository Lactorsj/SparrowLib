package cn.lactorsj.sparrowlib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

import cn.lactorsj.sparrowlib.MyApplication;
import cn.lactorsj.sparrowlib.entity.Book;
import cn.lactorsj.sparrowlib.entity.User;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private MyApplication app;
    public static final String DB_NAME = "users.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_BOOK = "book";
    private static UserDatabaseHelper mHelper = null;
    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_STATUS + " INT, " +
                    COLUMN_BOOK + " INT);";


    private UserDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static UserDatabaseHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new UserDatabaseHelper(context);
        }
        return mHelper;
    }

    public SQLiteDatabase openReadLink() {
        if (mRDB == null || !mRDB.isOpen()) {
            mRDB = mHelper.getReadableDatabase();
        }
        return mRDB;
    }

    public SQLiteDatabase openWriteLink() {
        if (mWDB == null || !mWDB.isOpen()) {
            mWDB = mHelper.getWritableDatabase();
        }
        return mWDB;
    }

    public void closeLink() {
        if (mRDB != null && mRDB.isOpen()) {
            mRDB.close();
            mRDB = null;
        }
        if (mWDB != null && mWDB.isOpen()) {
            mWDB.close();
            mWDB = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    public long insert(User user){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, user.username);
        contentValues.put(COLUMN_PASSWORD, user.password);
        contentValues.put(COLUMN_STATUS, user.status);
        contentValues.put(COLUMN_BOOK, user.book);
        return mWDB.insert(TABLE_NAME, null, contentValues);
    }

    public User queryUserByUsername(String username){
        User user = null;
        Cursor cursor = mRDB.query(TABLE_NAME, null, "username=?", new String[]{username}, null, null, null);
        if (cursor.moveToNext()) {
            user = new User();
            user.id = cursor.getInt(0);
            user.username = cursor.getString(1);
            user.password = cursor.getString(2);
            user.status = cursor.getInt(3);
            user.book = cursor.getInt(4);
        }
        cursor.close();
        return user;
    }

    public int borrowBookByUser(int book_id) {
        app = MyApplication.getInstance();
        String username = app.infoMap.get("username");
        User user = queryUserByUsername(username);
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, user.id);
        values.put(COLUMN_USERNAME, user.username);
        values.put(COLUMN_PASSWORD, user.password);
        values.put(COLUMN_STATUS, 0); // Cannot borrow book
        values.put(COLUMN_BOOK, book_id);
        int a = mRDB.update(TABLE_NAME, values, "username=?", new String[]{username});
        return a;
    }
    public int returnBookByUser(int book_id) {
        app = MyApplication.getInstance();
        String username = app.infoMap.get("username");
        User user = queryUserByUsername(username);
        if (user.book != book_id){
            return 0;
        }
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, user.id);
        values.put(COLUMN_USERNAME, user.username);
        values.put(COLUMN_PASSWORD, user.password);
        values.put(COLUMN_STATUS, 1); // Cannot borrow book
        values.put(COLUMN_BOOK, 0);
        int a = mRDB.update(TABLE_NAME, values, "username=?", new String[]{username});
        return a;
    }

    public boolean getCurrentUserStatus(){
        app = MyApplication.getInstance();
        User user = queryUserByUsername(app.infoMap.get("username"));
        if (user.status == 1) return true;
        return false;
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
