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
import com.flaremars.markandnote.bean.Result;
import com.flaremars.markandnote.common.callback.SimpleCallback;
import com.flaremars.markandnote.storage.UserStorage;
import com.flaremars.markandnote.util.StringUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by FlareMars on 2016/12/14
 */
public class RegisterActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private EditText phoneEt;
    private EditText pwdEt;
    private EditText verificationCodeEt;
    private EditText usernameEt;
    private Button registerBtn;
    private Button getVerificationCodeBtn;

    private InputMethodManager inputMethodManager;

    private ScheduledExecutorService timer;
    private static final int ONE_MINUTE = 60;
    private int curTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        phoneEt = (EditText) findViewById(R.id.accout_phone);
        pwdEt = (EditText) findViewById(R.id.accout_pwd);
        verificationCodeEt = (EditText) findViewById(R.id.et_verification_code);
        usernameEt = (EditText) findViewById(R.id.accout_username);
        registerBtn = (Button) findViewById(R.id.btn_register);
        getVerificationCodeBtn = (Button) findViewById(R.id.btn_get_verification_code);

        initViews();
    }

    private void initViews() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("请稍候...");
        progressDialog.setCancelable(false);

        getVerificationCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneEt.getText().toString();
                if (StringUtils.INSTANCE.isEmpty(phone)) {
                    Toast.makeText(RegisterActivity.this, "请填写正确的手机号码", Toast.LENGTH_SHORT).show();
                } else {
                    getVerificationCode(phone);
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneEt.getText().toString();
                String verificationCode = verificationCodeEt.getText().toString();
                String pwd = pwdEt.getText().toString();
                String username = usernameEt.getText().toString();

                if (StringUtils.INSTANCE.isEmpty(phone)) {
                    Toast.makeText(RegisterActivity.this, "手机号码不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.INSTANCE.isEmpty(verificationCode)) {
                    Toast.makeText(RegisterActivity.this, "验证码不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.INSTANCE.isEmpty(pwd)) {
                    Toast.makeText(RegisterActivity.this, "密码不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.INSTANCE.isEmpty(username)) {
                    Toast.makeText(RegisterActivity.this, "用户昵称不可为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog.show();
                UserStorage.getInstance().register(phone, verificationCode, username, pwd, new SimpleCallback() {
                    @Override
                    public void onCallback(Result result) {
                        progressDialog.dismiss();
                        if (result.getCode().equals(Result.CODE_SUCCESS)) {
                            Intent intent = new Intent(RegisterActivity.this, NotesMainActivity.class);
                            startActivity(intent);
                            RegisterActivity.this.finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "注册失败: " + result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void getVerificationCode(String phone) {
        timingBegins();
        Toast.makeText(RegisterActivity.this, "验证码发送成功，请注意查收", Toast.LENGTH_SHORT).show();
    }

    private void timingBegins() {
        curTime = ONE_MINUTE;
        getVerificationCodeBtn.setEnabled(false);
        timer = Executors.newScheduledThreadPool(1);
        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        curTime--;
                        if (curTime == 0) {
                            timer.shutdown();
                            timer = null;
                            getVerificationCodeBtn.setEnabled(true);
                            getVerificationCodeBtn.setText("获取验证码");
                            getVerificationCodeBtn.setBackgroundResource(R.drawable.bg_blue_with_deep_blue_stroke);
                            getVerificationCodeBtn.setTextColor(getResources().getColor(R.color.white));
                        } else {
                            getVerificationCodeBtn.setText(curTime + "秒后重发");
                        }
                    }
                });
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
}
