package jp.co.pannacotta.norokoro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler(Looper.getMainLooper()).postDelayed({
            if (!isFinishing) {
                // isFinishingでチェックしないとバックキーを押したとき登録画面にいってしまう
                val intent = Intent(this@SplashActivity, TourokuActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}