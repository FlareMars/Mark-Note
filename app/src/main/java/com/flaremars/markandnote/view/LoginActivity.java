package com.flaremars.markandnote.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flaremars.markandnote.R;
import com.flaremars.markandnote.entity.User;
import com.flaremars.markandnote.service.SynchronousService;
import com.flaremars.markandnote.storage.UserStorage;
import com.flaremars.markandnote.util.StringUtils;

/**
 * Created by FlareMars on 2016/12/14
 */
public class LoginActivity extends AppCompatActivity {

    private ProgressDialog loginDialog;
    private EditText phoneEt;
    private EditText pwdEt;
    private Button loginBtn;
    private Button registerBtn;

    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        phoneEt = (EditText) findViewById(R.id.accout_phone);
        pwdEt = (EditText) findViewById(R.id.accout_pwd);
        loginBtn = (Button) findViewById(R.id.btn_login);
        registerBtn = (Button) findViewById(R.id.btn_register);

        initViews();
    }

    private void initViews() {
        loginDialog = new ProgressDialog(this);
        loginDialog.setMessage("登录中...");
        loginDialog.setCancelable(false);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inputMethodManager.hideSoftInputFromWindow(pwdEt.getWindowToken(), 0);
                String phone = phoneEt.getText().toString();
                String password = pwdEt.getText().toString();
                if (StringUtils.INSTANCE.isEmpty(phone) || StringUtils.INSTANCE.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "手机号/密码不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginDialog.show();

                UserStorage.getInstance().login(phone, password, new UserStorage.LoginListener() {
                    @Override
                    public void onLoginSuccess(User user) {
                        loginDialog.dismiss();
                        Intent intent = new Intent(LoginActivity.this, NotesMainActivity.class);
                        startActivity(intent);

                        Intent pullNotesData = new Intent(LoginActivity.this, SynchronousService.class);
                        pullNotesData.putExtra(SynchronousService.ACTION_INTENT, SynchronousService.ACTION_PULL);
                        startService(pullNotesData);

                        LoginActivity.this.finish();
                    }

                    @Override
                    public void onLoginFail(String message) {
                        loginDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "登录失败: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
                LoginActivity.this.finish();
            }
        });
    }

}
