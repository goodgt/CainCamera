package com.demo.greenmatting;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.greenmatting.network.AddCookiesInterceptor;
import com.demo.greenmatting.network.BaseRes;
import com.demo.greenmatting.network.MyCallback;
import com.demo.greenmatting.network.NetworkRequestUtils;
import com.demo.greenmatting.utils.SPUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.widget.FButton;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.username)
    TextInputEditText username;
    @BindView(R.id.password)
    TextInputEditText password;
    @BindView(R.id.username_layout)
    TextInputLayout usernameLayout;
    @BindView(R.id.password_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        username.setText(SPUtil.getStringSpVal(this, "username"));
        username.setSelection(username.getText().length());
        password.setText(SPUtil.getStringSpVal(this, "pwd"));
        password.setSelection(password.getText().length());

        FButton fButton = findViewById(R.id.user_login);
        fButton.setButtonColor(getResources().getColor(R.color.colorPrimary));
        FButton fButton1 = findViewById(R.id.user_reg);
        fButton1.setButtonColor(getResources().getColor(R.color.colorPrimary));
    }

    @OnClick({R.id.user_login, R.id.user_reg})
    public void onViewClicked(View view) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username.getText().toString());
        params.put("pwd", password.getText().toString());
        switch (view.getId()) {
            case R.id.user_login:
                tvTitle.setText("登录");
                if (TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(password.getText())) {
                    Toast.makeText(this, "请输入用户名或密码", Toast.LENGTH_LONG).show();
                    return;
                }
                new NetworkRequestUtils().networkRequest("userLogin", params, new MyCallback<BaseRes<Result>>() {
                    @Override
                    public void loadingDataSuccess(BaseRes<Result> result) {
                        if (result.getCode() == 20) {
                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                            AddCookiesInterceptor.token = result.getResult().getToken();

                            SPUtil.changeStringSp(LoginActivity.this, "username", username.getText().toString());
                            SPUtil.changeStringSp(LoginActivity.this, "pwd", password.getText().toString());
                            SPUtil.changeIntSp(LoginActivity.this, "userid", result.getResult().getUserInfo().getUserid());
                            onBackPressed();
                        } else {
//                            usernameLayout.setError(result.getMessage());
                            Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
            case R.id.user_reg:
                tvTitle.setText("注册");
                if (TextUtils.isEmpty(username.getText()) || TextUtils.isEmpty(password.getText())) {
                    Toast.makeText(this, "请输入用户名或密码", Toast.LENGTH_LONG).show();
                    return;
                }
                new NetworkRequestUtils().networkRequest("userReg", params, new MyCallback<BaseRes>() {
                    @Override
                    public void loadingDataSuccess(BaseRes result) {
                        if (result.getCode() == 20) {
                            Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                        } else {
//                            passwordLayout.setError(result.getMessage());
                            Toast.makeText(LoginActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
        }
    }

    public class Result {
        public String token;
        public UserInfoBean userInfo;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public UserInfoBean getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(UserInfoBean userInfo) {
            this.userInfo = userInfo;
        }
    }

    public class UserInfoBean {
        public String username;
        public int userid;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public int getUserid() {
            return userid;
        }

        public void setUserid(int userid) {
            this.userid = userid;
        }
    }
}
