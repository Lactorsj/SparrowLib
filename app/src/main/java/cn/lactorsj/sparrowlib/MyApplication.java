package cn.lactorsj.sparrowlib;

import android.app.Application;

import java.util.HashMap;
import java.util.List;

import cn.lactorsj.sparrowlib.database.BookDatabaseHelper;
import cn.lactorsj.sparrowlib.database.UserDatabaseHelper;
import cn.lactorsj.sparrowlib.entity.Book;
import cn.lactorsj.sparrowlib.util.SharedUtil;


public class MyApplication extends Application {

    private static MyApplication mApp;

    public HashMap<String, String> infoMap; // Global Variables
    private BookDatabaseHelper bookDatabaseHelper;
    private UserDatabaseHelper userDatabaseHelper;

    public static MyApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mApp = this;
        infoMap = new HashMap<>();
        initBooksInfo();
    }

    private void initBooksInfo() {
        boolean first = SharedUtil.getInstance(this).readBoolean("first", true);
        if (first) {
            bookDatabaseHelper = BookDatabaseHelper.getInstance(this);
            userDatabaseHelper = UserDatabaseHelper.getInstance(this);
            List<Book> list = Book.getDefaultList();
            bookDatabaseHelper.openWriteLink();
            bookDatabaseHelper.insertBooksInfo(list);
            bookDatabaseHelper.closeLink();
            SharedUtil.getInstance(this).writeBoolean("first", false);
        }
    }

    @Override
    public void onTerminate() {
        userDatabaseHelper.closeLink();
        bookDatabaseHelper.closeLink();
        super.onTerminate();
    }
}
