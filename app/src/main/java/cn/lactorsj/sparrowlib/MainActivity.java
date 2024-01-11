package cn.lactorsj.sparrowlib;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.lactorsj.sparrowlib.database.BookDatabaseHelper;
import cn.lactorsj.sparrowlib.entity.Book;


public class MainActivity extends AppCompatActivity {

    private MyApplication app;
    private Context context;
    private LinearLayout ll_home;
    private BookDatabaseHelper mDBHelper;
    private boolean backPressedOnce = false;
    private int flag = 1;
    private TextView tv_is_available;
    private Button btn_borrow;
    private Button btn_return;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = MyApplication.getInstance();

        TextView tv_hello = findViewById(R.id.tv_hello);
        tv_hello.setText(String.format("Hello! %s", app.infoMap.get("username")));
        mDBHelper = BookDatabaseHelper.getInstance(this);
        mDBHelper.openReadLink();
        mDBHelper.openWriteLink();

        ll_home = findViewById(R.id.ll_home);
        // 从数据库查询出商品信息，并展示
        showBooks();
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

//    private void updateButton() {
//        if (flag == 1) {
//            btn_borrow_or_return.setEnabled(true);
//            tv_is_available.setText("Currently in library");
//            tv_is_available.setTextColor(getColor(R.color.gray));
//            btn_borrow_or_return.setText("Borrow Now");
//
//        } else if (flag == 0) {
//            btn_borrow_or_return.setEnabled(true);
//            tv_is_available.setText("Not in library: Borrowed by you");
//            tv_is_available.setTextColor(getColor(R.color.gray));
//            btn_borrow_or_return.setText("Return Now");
//        }
//    }

    private void showBooks() {
        // 商品条目是一个线性布局，设置布局的宽度为屏幕的一半
        // 查询商品数据库中的所有商品记录
        List<Book> list = mDBHelper.queryAllBooksInfo();

        // 移除下面的所有子视图
        ll_home.removeAllViews();

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

            if (info.isAvailable == 1) {
                flag = 1; // can borrow
                btn_return.setEnabled(false);
                btn_borrow.setEnabled(true);
                tv_is_available.setText("Currently in library");
                tv_is_available.setTextColor(Color.GRAY);
            } else if (info.isAvailable == 0 && info.borrowBy.equals(app.infoMap.get("username"))) {
                flag = 0; // can return
                btn_borrow.setEnabled(false);
                btn_return.setEnabled(true);
                tv_is_available.setText("Not in library: borrowed by you.");
                tv_is_available.setTextColor(Color.RED);
            } else {
                flag = -1; // cannot do anything
                btn_return.setEnabled(false);
                btn_borrow.setEnabled(false);
                tv_is_available.setText(String.format("Not in library: borrowed by %s", info.borrowBy));
            }

            btn_borrow.setOnClickListener(v -> {
                if (mDBHelper.borrowBook(info.id) > 0) {
                    Toast.makeText(this, "Borrowed Successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Borrowed Failed", Toast.LENGTH_SHORT).show();
                }
                flag = 0;
                btn_borrow.setEnabled(false);
                btn_return.setEnabled(true);
                recreate();
            });


            btn_return.setOnClickListener(v -> {
                if (mDBHelper.returnBook(info.id) > 0) {
                    Toast.makeText(this, "Returned Successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Returned Failed", Toast.LENGTH_SHORT).show();
                }
                flag = 0;
                btn_return.setEnabled(false);
                btn_borrow.setEnabled(true);
                recreate();
            });

//            updateButton();

//            if (info.isAvailable == 0 && !info.borrowBy.equals(app.infoMap.get("username"))) {
//                flag = -1;
//                tv_is_available.setText(String.format("Not in library: borrowed by %s", info.borrowBy));
//                btn_borrow_or_return.setText("Not Available");
//                btn_borrow_or_return.setEnabled(false);
//            }
//
//            btn_borrow_or_return.setOnClickListener(v -> {
//                if (flag == 1) {
//                    if (mDBHelper.borrowBook(info.id) > 0) {
//                        Toast.makeText(this, "Borrowed Successful!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(this, "Borrowed Failed", Toast.LENGTH_SHORT).show();
//                    }
//                    flag = 0;
//                } else if (flag == 0) {
//                    if (mDBHelper.returnBook(info.id) > 0) {
//                        Toast.makeText(this, "Returned Successful!", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(this, "Returned Failed", Toast.LENGTH_SHORT).show();
//                    }
//                    flag = 1;
//                }
//                updateButton();
//            });

            ll_home.addView(view);

        }
    }
}