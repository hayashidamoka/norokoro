package jp.co.pannacotta.norokoro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class WaraningyouActivity extends AppCompatActivity {
    int noroiCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waraningyou);

        ImageView dislikeImageView = findViewById(R.id.dislikeImageView);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String dislike_image_path = prefs.getString("DISLIKE_IMAGE_PATH",getString(R.string.blank));


        if(!TextUtils.isEmpty(dislike_image_path)) {
            //dislike_image_pathファイルをdislikeImageViewにintoする
            Picasso.get().load(new File(dislike_image_path)).into(dislikeImageView);
        }


        final TextView countTextView = findViewById(R.id.countTextView);
        ImageView kugibutton = findViewById(R.id.kugi);

        kugibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noroiCount = noroiCount + 1;
                String noroiCountText = String.valueOf(noroiCount);
                countTextView.setText(noroiCountText);

                Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(50);

                ImageView kugi_push =findViewById(R.id.kugi_push);
                if (noroiCount >= 5){
                    kugi_push.setVisibility(View.GONE);
                }

            }
        });
    }
}
