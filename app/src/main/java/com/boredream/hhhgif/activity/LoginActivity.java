package com.boredream.hhhgif.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.boredream.hhhgif.R;
import com.boredream.hhhgif.base.BaseActivity;
import com.boredream.hhhgif.entity.User;
import com.boredream.hhhgif.net.HttpRequest;
import com.boredream.hhhgif.net.SimpleObservableDecorator;

import rx.Observable;
import rx.functions.Action1;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText et_username;
    private EditText et_password;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
    }

    private void initView() {
        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        btn_login = (Button) findViewById(R.id.btn_login);

        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
        }
    }

    private void login() {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        Observable<User> observable = HttpRequest.getApiService().userLogin(username, password);
        SimpleObservableDecorator.decorate(observable, new Action1<User>() {
            @Override
            public void call(User user) {

            }
        });

    }
}
