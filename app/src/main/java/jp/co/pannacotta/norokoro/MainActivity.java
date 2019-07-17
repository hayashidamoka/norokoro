package jp.co.pannacotta.norokoro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView noroubutton = findViewById(R.id.norou_button);
        noroubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,WaraningyouActivity.class);
                startActivity(intent);
            }
        });
        final ImageView korosubutton = findViewById(R.id.korosu_button);
        korosubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent();
                intent.setClass(MainActivity.this,KorosuActivity.class);
                startActivity(intent);
            }
        });

        ImageView dislikeImageView = findViewById(R.id.dislike_image_view);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String dislike_name = prefs.getString("DISLIKE_NAME",getString(R.string.blank));
        String dislike_image_path = prefs.getString("DISLIKE_IMAGE_PATH",getString(R.string.blank));

        TextView nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(dislike_name);

        if(!TextUtils.isEmpty(dislike_image_path)) {
            //dislike_image_pathファイルをdislikeImageViewにintoする
            Picasso.get().load(new File(dislike_image_path)).into(dislikeImageView);
        }

    }
}
