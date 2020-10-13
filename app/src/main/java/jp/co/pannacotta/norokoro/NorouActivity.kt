package jp.co.pannacotta.norokoro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class NorouActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_norou)
        val noroubutton = findViewById<Button>(R.id.norou_button)
        noroubutton.setOnClickListener {
            val intent = Intent(this@NorouActivity, WaraningyouActivity::class.java)
            startActivity(intent)
        }
    }
}