package jp.co.pannacotta.norokoro

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.io.File

class WaraningyouActivity : AppCompatActivity() {
    var noroiCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waraningyou)
        val dislikeImageView = findViewById<ImageView>(R.id.dislikeImageView)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val dislike_image_path = prefs.getString("DISLIKE_IMAGE_PATH", getString(R.string.blank))
        if (!TextUtils.isEmpty(dislike_image_path)) {
            //dislike_image_pathファイルをdislikeImageViewにintoする
            Glide.with(this).load(File(dislike_image_path)).into(dislikeImageView)
        }
        val countTextView = findViewById<TextView>(R.id.countTextView)
        val kugibutton = findViewById<ImageView>(R.id.kugi)
        kugibutton.setOnClickListener {
            noroiCount = noroiCount + 1
            val noroiCountText = noroiCount.toString()
            countTextView.text = noroiCountText
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(50)
            val kugi_push = findViewById<ImageView>(R.id.kugi_push)
            if (noroiCount >= 5) {
                kugi_push.visibility = View.GONE
            }
        }
    }
}