package jp.co.pannacotta.norokoro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    // isFinishingでチェックしないとバックキーを押したとき登録画面にいってしまう
                    Intent intent = new Intent(SplashActivity.this, TourokuFragment.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }
}
