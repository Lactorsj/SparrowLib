package cn.lactorsj.sparrowlib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import cn.lactorsj.sparrowlib.MyApplication;
import cn.lactorsj.sparrowlib.entity.Book;

public class BookDatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "books.db";
    public static final String TABLE_NAME = "books";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_BOOK_NAME = "book_name";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_IS_AVAILABLE = "is_available";
    public static final String COLUMN_BORROWED_BY = "borrowed_by";
    public static final String COLUMN_PIC = "pic";
    private static final int DB_VERSION = 1;
    private static BookDatabaseHelper mHelper = null;
    private MyApplication app;
    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    private BookDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static BookDatabaseHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new BookDatabaseHelper(context);
        }
        return mHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BOOK_NAME + " VARCHAR, " +
                COLUMN_AUTHOR + " VARCHAR, " +
                COLUMN_IS_AVAILABLE + " INT, " +
                COLUMN_PIC + " INT, " +
                COLUMN_BORROWED_BY + " VARCHAR);";
        db.execSQL(sql);
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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertBooksInfo(List<Book> list) {
        try {
            mWDB.beginTransaction();
            for (Book info : list) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_BOOK_NAME, info.name);
                values.put(COLUMN_AUTHOR, info.author);
                values.put(COLUMN_IS_AVAILABLE, 1);
                values.put(COLUMN_PIC, info.pic);
                mWDB.insert(TABLE_NAME, null, values);
            }
            mWDB.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWDB.endTransaction();
        }
    }

    // 查询所有的商品信息
    public List<Book> queryAllBooksInfo() {
        String sql = "select * from " + TABLE_NAME;
        List<Book> list = new ArrayList<>();
        Cursor cursor = mRDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Book info = new Book();
            info.id = cursor.getInt(0);
            info.name = cursor.getString(1);
            info.author = cursor.getString(2);
            info.isAvailable = cursor.getInt(3);
            info.pic = cursor.getInt(4);
            info.borrowBy = cursor.getString(5);
            list.add(info);
        }
        cursor.close();
        return list;
    }

    public Book queryBookById(int id) {
        Book b = null;
        Cursor cursor = mRDB.query(TABLE_NAME, null, "_id=?",
                new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext()) {
            b = new Book();
            b.id = cursor.getInt(0);
            b.name = cursor.getString(1);
            b.author = cursor.getString(2);
            b.isAvailable = cursor.getInt(3);
            b.pic = cursor.getInt(4);
            b.borrowBy = cursor.getString(5);
        }
        cursor.close();
        return b;
    }

    public int borrowBook(int id) {
        Book b = queryBookById(id);
        app = MyApplication.getInstance();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, b.id);
        values.put(COLUMN_BOOK_NAME, b.name);
        values.put(COLUMN_AUTHOR, b.author);
        values.put(COLUMN_IS_AVAILABLE, 0); // put this book to not available
        values.put(COLUMN_BORROWED_BY, app.infoMap.get("username"));
        return mRDB.update(TABLE_NAME, values, "_id=?", new String[]{String.valueOf(id)});
    }

    public int returnBook(int id) {
        Book b = queryBookById(id);
        app = MyApplication.getInstance();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, b.id);
        values.put(COLUMN_BOOK_NAME, b.name);
        values.put(COLUMN_AUTHOR, b.author);
        values.put(COLUMN_IS_AVAILABLE, 1); // set this book to available
        return mRDB.update(TABLE_NAME, values, "_id=?", new String[]{String.valueOf(id)});
    }
}
