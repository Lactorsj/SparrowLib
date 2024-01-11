package cn.lactorsj.sparrowlib;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import cn.lactorsj.sparrowlib.database.BookDatabaseHelper;
import cn.lactorsj.sparrowlib.database.UserDatabaseHelper;
import cn.lactorsj.sparrowlib.entity.Book;
import cn.lactorsj.sparrowlib.entity.User;


public class MainActivity extends AppCompatActivity {

    private MyApplication app;
    private Context context;
    private LinearLayout ll_home;
    private UserDatabaseHelper UserDBHelper;
    private BookDatabaseHelper BookDBHelper;
    private boolean backPressedOnce = false;
    private TextView tv_is_available;
    private Button btn_borrow;
    private Button btn_return;
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
        // 从数据库查询出商品信息，并展示
        showBooks();

        Button btn_log_out = findViewById(R.id.btn_log_out);
        btn_log_out.setOnClickListener(v -> {
            app.infoMap.remove("username");
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    @SuppressLint("MissingSuperCall")
    public void onBackPressed() {
        if (backPressedOnce) {
            backToHomeScreen(); // 如果之前已经按过一次返回键，执行默认的返回操作
            return;
        }

        // 第一次按下返回键，显示提示
        Toast.makeText(this, "Press back again to go back to the home screen", Toast.LENGTH_SHORT).show();

        backPressedOnce = true;

        // 延迟一段时间后重置 backPressedOnce，这里设置为2秒
        new Handler().postDelayed(() -> backPressedOnce = false, 2000);
    }

    private void backToHomeScreen() {
        // 使用Intent回到主屏幕
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }

    private void showBooks() {

        // 查询商品数据库中的所有商品记录
        List<Book> list = BookDBHelper.queryAllBooksInfo();

        User user = UserDBHelper.queryUserByUsername(app.infoMap.get("username"));

        // 移除下面的所有子视图
        ll_home.removeAllViews();
        book_views = new ArrayList<>();
        for (Book info : list) {
            // 获取布局文件item_goods.xml的根视图
            View view = LayoutInflater.from(this).inflate(R.layout.element_item_book, null);
            ImageView iv_thumb = view.findViewById(R.id.iv_thumb);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_author = view.findViewById(R.id.tv_author);
            tv_is_available = view.findViewById(R.id.tv_is_available);
            btn_borrow = view.findViewById(R.id.btn_borrow);
            btn_return = view.findViewById(R.id.btn_return);

            // 给控件设置值
            iv_thumb.setImageResource(info.pic);
            tv_name.setText(info.name);
            tv_author.setText(info.author);

            if (UserDBHelper.getCurrentUserStatus()) {//Can borrow books
                if (info.isAvailable == 1) {
                    btn_return.setEnabled(false);
                    btn_borrow.setEnabled(true);
                    tv_is_available.setText("Currently in library");
                    tv_is_available.setTextColor(Color.GRAY);
                } else {
                    btn_borrow.setEnabled(false);
                    btn_return.setEnabled(false);
                    tv_is_available.setText(String.format("Not in library: borrowed by %s", info.borrowBy));
                    tv_is_available.setTextColor(Color.RED);
                }
            } else {
                if (info.id != user.book && info.isAvailable == 1) {
                    btn_borrow.setEnabled(false);
                    btn_return.setEnabled(false);
                    tv_is_available.setText("You can only borrow one book");
                    tv_is_available.setTextColor(Color.RED);
                } else if (info.id != user.book && info.isAvailable == 0){
                    btn_borrow.setEnabled(false);
                    btn_return.setEnabled(false);
                    tv_is_available.setText(String.format("Not in library: borrowed by %s", info.borrowBy));
                    tv_is_available.setTextColor(Color.RED);
                } else {
                    btn_borrow.setEnabled(false);
                    btn_return.setEnabled(true);
                    tv_is_available.setText("Borrowed by you");
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
                        tv_view_is_available.setText("You can only borrow one book");
                        tv_view_is_available.setTextColor(Color.RED);
                    }
                    if (i == info.id - 1) {
                        btn_view_borrow.setEnabled(false);
                        btn_view_return.setEnabled(true);
                        tv_view_is_available.setText("Borrowed by you");
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
                        tv_view_is_available.setText("Currently in library");
                        tv_view_is_available.setTextColor(Color.GRAY);
                    }
                }
            });


            ll_home.addView(view);
            book_views.add(view);
        }
    }

}