package cn.lactorsj.sparrowlib;

import android.app.Application;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

import cn.lactorsj.sparrowlib.database.BookDatabaseHelper;
import cn.lactorsj.sparrowlib.entity.Book;
import cn.lactorsj.sparrowlib.util.SharedUtil;


public class MyApplication extends Application {

    private static MyApplication mApp;

    public HashMap<String, String> infoMap;

    public static MyApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        mApp = this;
//        Log.d("Jeffrey", "MyApplication onCreate() called");
        infoMap = new HashMap<>();
        initBooksInfo();
    }
    private void initBooksInfo(){
        boolean first = SharedUtil.getInstance(this).readBoolean("first", true);
        if (first) {
            BookDatabaseHelper dbHelper = BookDatabaseHelper.getInstance(this);
            List<Book> list = Book.getDefaultList();
            dbHelper.openWriteLink();
            dbHelper.insertBooksInfo(list);
            dbHelper.closeLink();
            SharedUtil.getInstance(this).writeBoolean("first", false);
        }
    }
}
