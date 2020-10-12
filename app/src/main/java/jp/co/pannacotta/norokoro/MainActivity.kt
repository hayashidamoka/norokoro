package jp.co.pannacotta.norokoro

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val noroubutton = findViewById<ImageView>(R.id.norou_button)
        val korosubutton = findViewById<ImageView>(R.id.korosu_button)
        val dislikeImageView = findViewById<ImageView>(R.id.dislike_image_view)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val dislike_name = prefs.getString("DISLIKE_NAME", getString(R.string.blank))
        val dislike_image_path = prefs.getString("DISLIKE_IMAGE_PATH", getString(R.string.blank))
        val nameTextView = findViewById<TextView>(R.id.nameTextView)

        noroubutton.setOnClickListener {
            val intent = Intent(this@MainActivity, WaraningyouActivity::class.java)
            startActivity(intent)
        }

        korosubutton.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@MainActivity, KorosuActivity::class.java)
            startActivity(intent)
        }

        nameTextView.text = dislike_name
        if (!TextUtils.isEmpty(dislike_image_path)) {
            //dislike_image_pathファイルをdislikeImageViewにintoする
            Picasso.get().load(File(dislike_image_path)).into(dislikeImageView)
        }
    }
}