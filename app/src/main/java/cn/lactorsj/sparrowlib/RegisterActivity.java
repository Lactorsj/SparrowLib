package cn.lactorsj.sparrowlib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.lactorsj.sparrowlib.database.UserDatabaseHelper;
import cn.lactorsj.sparrowlib.entity.User;

public class RegisterActivity extends AppCompatActivity {


    private UserDatabaseHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        Toolbar toolbar = findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button btn_register = findViewById(R.id.btn_register);
        EditText et_username = findViewById(R.id.et_username);
        EditText et_password = findViewById(R.id.et_password);
        EditText et_retype_password = findViewById(R.id.et_retype_password);

        et_username.setSingleLine();

        et_username.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                et_password.requestFocus();
                return true;
            }
            return false;
        });

        et_password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                et_retype_password.requestFocus();
                return true;
            }
            return false;
        });


        et_retype_password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_retype_password.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                String retype_password = et_retype_password.getText().toString();

                if (!password.equals(retype_password)) {
                    Toast.makeText(RegisterActivity.this, "Password do not match", Toast.LENGTH_SHORT).show();
                } else if (checkUsernameExist(username)) {
                    Toast.makeText(RegisterActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                } else {
                    createUser(username, password);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getOnBackPressedDispatcher().onBackPressed();
                            finish();
                        }
                    }, 1000);
                }
            }
        });


    }

    private void createUser(String username, String password) {
        User user = new User(username, password);
        if (mHelper.insert(user) > 0) {
            Toast.makeText(RegisterActivity.this, "Successfully Create User", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkUsernameExist(String username) {

        SQLiteDatabase db = mHelper.openReadLink();
        String[] projection = {
                UserDatabaseHelper.COLUMN_USERNAME
        };

        String selection = UserDatabaseHelper.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(
                UserDatabaseHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        boolean usernameExists = cursor != null && cursor.getCount() > 0;

        if (cursor != null) {
            cursor.close();
        }

        return usernameExists;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHelper = UserDatabaseHelper.getInstance(this);
        mHelper.openReadLink();
        mHelper.openWriteLink();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mHelper.closeLink();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}