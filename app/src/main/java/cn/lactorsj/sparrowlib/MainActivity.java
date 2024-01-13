package cn.lactorsj.sparrowlib;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import cn.lactorsj.sparrowlib.database.BookDatabaseHelper;
import cn.lactorsj.sparrowlib.database.UserDatabaseHelper;
import cn.lactorsj.sparrowlib.entity.Book;
import cn.lactorsj.sparrowlib.entity.User;


public class MainActivity extends AppCompatActivity {

    private MyApplication app;
    private LinearLayout ll_home;
    private UserDatabaseHelper UserDBHelper;
    private BookDatabaseHelper BookDBHelper;
    private boolean backPressedOnce = false;
    private List<View> book_views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = MyApplication.getInstance();

        TextView tv_hello = findViewById(R.id.tv_hello);
        tv_hello.setText(String.format("Hello! %s", app.infoMap.get("username")));
        BookDBHelper = BookDatabaseHelper.getInstance(this);
        UserDBHelper = UserDatabaseHelper.getInstance(this);
        BookDBHelper.openReadLink();
        BookDBHelper.openWriteLink();
        UserDBHelper.openWriteLink();
        UserDBHelper.openReadLink();


        ll_home = findViewById(R.id.ll_home);
        showBooks();

        Button btn_log_out = findViewById(R.id.btn_log_out);
        btn_log_out.setOnClickListener(v -> {
            app.infoMap.remove("username");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    /** @noinspection deprecation*/
    @SuppressLint("MissingSuperCall")
    public void onBackPressed() {
        if (backPressedOnce) {
            backToHomeScreen(); // press back twice to back to system Home screen
            return;
        }

        // on backPressedOnce
        Toast.makeText(this, "Press back again to go back to the home screen", Toast.LENGTH_SHORT).show();

        backPressedOnce = true;

        // after delay reset backPressedOnce
        new Handler().postDelayed(() -> backPressedOnce = false, 2000);
    }

    private void backToHomeScreen() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    private void showBooks() {

        List<Book> list = BookDBHelper.queryAllBooksInfo();
        User user = UserDBHelper.queryUserByUsername(app.infoMap.get("username"));

        ll_home.removeAllViews();
        book_views = new ArrayList<>();
        for (Book info : list) {
            // Get element layout
            @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.element_item_book, null);
            ImageView iv_thumb = view.findViewById(R.id.iv_thumb);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_author = view.findViewById(R.id.tv_author);
            TextView tv_is_available = view.findViewById(R.id.tv_is_available);
            Button btn_borrow = view.findViewById(R.id.btn_borrow);
            Button btn_return = view.findViewById(R.id.btn_return);

            // set values to TextView and Button
            iv_thumb.setImageResource(info.pic);
            tv_name.setText(info.name);
            tv_author.setText(info.author);

            if (UserDBHelper.getCurrentUserStatus()) {//Can borrow books
                if (info.isAvailable == 1) { // available books
                    btn_return.setEnabled(false);
                    btn_borrow.setEnabled(true);
                    tv_is_available.setText(R.string.available);
                    tv_is_available.setTextColor(Color.GRAY);
                } else { // borrowed by others
                    btn_borrow.setEnabled(false);
                    btn_return.setEnabled(false);
                    tv_is_available.setText(String.format("Not in library: borrowed by %s", info.borrowBy));
                    tv_is_available.setTextColor(Color.RED);
                }
            } else { // cannot borrow books
                if (info.id != user.book && info.isAvailable == 1) { // available books, return first
                    btn_borrow.setEnabled(false);
                    btn_return.setEnabled(false);
                    tv_is_available.setText(R.string.return_first);
                    tv_is_available.setTextColor(Color.RED);
                } else if (info.id != user.book && info.isAvailable == 0) { // borrowed by others
                    btn_borrow.setEnabled(false);
                    btn_return.setEnabled(false);
                    tv_is_available.setText(String.format("Not in library: borrowed by %s", info.borrowBy));
                    tv_is_available.setTextColor(Color.RED);
                } else { // borrowed by you
                    btn_borrow.setEnabled(false);
                    btn_return.setEnabled(true);
                    tv_is_available.setText(R.string.borrowed_by_you);
                    tv_is_available.setTextColor(Color.GRAY);
                }
            }


            btn_borrow.setOnClickListener(v -> {
                if (BookDBHelper.borrowBook(info.id) > 0 && UserDBHelper.borrowBookByUser(info.id) > 0) {
                    Toast.makeText(this, "Borrowed Successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Borrowed Failed", Toast.LENGTH_SHORT).show();
                }

                int view_cnt = book_views.size();

                for (int i = 0; i < view_cnt; i++) {
                    Button btn_view_borrow = book_views.get(i).findViewById(R.id.btn_borrow);
                    Button btn_view_return = book_views.get(i).findViewById(R.id.btn_return);
                    TextView tv_view_is_available = book_views.get(i).findViewById(R.id.tv_is_available);
                    if (list.get(i).isAvailable == 1 && i != info.id - 1) {
                        btn_view_borrow.setEnabled(false);
                        btn_view_return.setEnabled(false);
                        tv_view_is_available.setText(R.string.return_first);
                        tv_view_is_available.setTextColor(Color.RED);
                    }
                    if (i == info.id - 1) {
                        btn_view_borrow.setEnabled(false);
                        btn_view_return.setEnabled(true);
                        tv_view_is_available.setText(R.string.borrowed_by_you);
                        tv_view_is_available.setTextColor(Color.GRAY);
                    }
                }
            });


            btn_return.setOnClickListener(v -> {
                if (BookDBHelper.returnBook(info.id) > 0 && UserDBHelper.returnBookByUser(info.id) > 0) {
                    Toast.makeText(this, "Returned Successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Returned Failed", Toast.LENGTH_SHORT).show();
                }

                int view_cnt = book_views.size();
                for (int i = 0; i < view_cnt; i++) {
                    Button btn_view_borrow = book_views.get(i).findViewById(R.id.btn_borrow);
                    Button btn_view_return = book_views.get(i).findViewById(R.id.btn_return);
                    TextView tv_view_is_available = book_views.get(i).findViewById(R.id.tv_is_available);
                    if (list.get(i).isAvailable == 1 || i == info.id - 1) {
                        btn_view_borrow.setEnabled(true);
                        btn_view_return.setEnabled(false);
                        tv_view_is_available.setText(R.string.available);
                        tv_view_is_available.setTextColor(Color.GRAY);
                    }
                }
            });


            ll_home.addView(view);
            book_views.add(view);
        }
    }

}