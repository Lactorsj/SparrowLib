package cn.lactorsj.sparrowlib;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import cn.lactorsj.sparrowlib.database.UserDatabaseHelper;
import cn.lactorsj.sparrowlib.entity.User;


public class LoginActivity extends AppCompatActivity {

    private MyApplication app;
    private UserDatabaseHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        app = MyApplication.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Objects.requireNonNull(getSupportActionBar()).setTitle("Login");


        Button btn_login = findViewById(R.id.btn_login);
        Button btn_register = findViewById(R.id.btn_register);
        EditText et_username = findViewById(R.id.et_username);
        EditText et_password = findViewById(R.id.et_password);

//        et_password.setSingleLine();
        et_username.setSingleLine();

        et_username.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                et_password.requestFocus();
                return true;
            }
            return false;
        });

        et_password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_password.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        btn_login.setOnClickListener(v -> {
            String username = et_username.getText().toString();
            String password = et_password.getText().toString();

            if (validateUser(username, password)) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                app.infoMap.put("username", username);
                startActivity(intent);
            } else {
                Toast.makeText(LoginActivity.this, "Wrong Username or Password!", Toast.LENGTH_SHORT).show();
            }
        });

        btn_register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateUser(String username, String password) {

        SQLiteDatabase db = mHelper.openWriteLink();

        try {
            String[] projection = {
                    UserDatabaseHelper.COLUMN_USERNAME,
                    UserDatabaseHelper.COLUMN_PASSWORD
            };

            String selection = UserDatabaseHelper.COLUMN_USERNAME + " = ? AND " +
                    UserDatabaseHelper.COLUMN_PASSWORD + " = ?";
            String[] selectionArgs = {username, password};

            Cursor cursor = db.query(
                    UserDatabaseHelper.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper = UserDatabaseHelper.getInstance(this);
        mHelper.openReadLink();
//        mHelper.openWriteLink();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mHelper.closeLink();
    }
}
