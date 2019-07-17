package jp.co.pannacotta.norokoro;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

//あぷり
public class NorouActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_norou);

        music_start();

        Button noroubutton = findViewById(R.id.norou_button);
        noroubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NorouActivity.this, WaraningyouActivity.class);
                startActivity(intent);
            }
        });

    }
    private void music_start(){
        mediaPlayer = MediaPlayer.create(getBaseContext(),R.raw.norousong);
        mediaPlayer.setLooping(true);
        mediaPlayer.seekTo(0);
        mediaPlayer.start();
    }

}
