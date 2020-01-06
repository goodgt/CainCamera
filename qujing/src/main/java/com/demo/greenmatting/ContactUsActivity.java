package com.demo.greenmatting;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.demo.greenmatting.network.BaseRes;
import com.demo.greenmatting.network.MyCallback;
import com.demo.greenmatting.network.NetworkRequestUtils;
import com.demo.greenmatting.utils.SPUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.gt.utils.view.OnNoDoubleClickListener;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import info.hoang8f.widget.FButton;

public class ContactUsActivity extends AppCompatActivity {

    @BindView(R.id.phone_view)
    TextInputEditText phoneView;
    @BindView(R.id.context_view)
    TextInputEditText contextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us_layout);
        ButterKnife.bind(this);

        ActionBar actionBar = this.getSupportActionBar();
        // 显示返回按钮
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("联系我们");

        FButton fButton = findViewById(R.id.submission);
        fButton.setButtonColor(getResources().getColor(R.color.colorPrimary));

        findViewById(R.id.submission).setOnClickListener(new OnNoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                Map<String, Object> params = new HashMap<>();
                params.put("userid", SPUtil.getIntSpVal(ContactUsActivity.this, "userid"));
                params.put("mobile", phoneView.getText().toString());
                params.put("content", contextView.getText().toString());
                new NetworkRequestUtils().networkRequest("userLianxi", params, new MyCallback<BaseRes>() {
                    @Override
                    public void loadingDataSuccess(BaseRes result) {
                        if (result.getCode() == 20 && result.getResult() != null) {
                            Toast.makeText(ContactUsActivity.this, "提交成功", Toast.LENGTH_LONG).show();
                        } else
                            Toast.makeText(ContactUsActivity.this, result.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
