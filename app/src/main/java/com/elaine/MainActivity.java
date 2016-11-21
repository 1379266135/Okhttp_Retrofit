package com.elaine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.elaine.okretrolib.CallbackListener;
import com.elaine.okretrolib.RequestUtils;
import com.elaine.response.UserInfoResp;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.request)
    TextView request;
    @Bind(R.id.activity_main)
    RelativeLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                RequestUtils.getInstance().loadByPost( UserInfoResp.class, Urls.User.generateSession, null, new CallbackListener<UserInfoResp>() {

                    @Override
                    public void onSuccess(UserInfoResp data, String msg) {
                        Log.e("OKhttp", data.getUserInfo());
                    }

                    @Override
                    public void onSuccess(List<UserInfoResp> data, String msg) {
                        Log.e("OKhttp", data.get(0).getUserInfo());
                    }

                    @Override
                    public void onFail(String msg) {
                        Log.e("OKhttp", msg);
                    }
                });
            }
        });
    }
}
