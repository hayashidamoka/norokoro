package com.uchinokomoratte.norokoro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class WaraningyouActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waraningyou);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);


        int gazouNoID = prefs.getInt("GAZOU_NO_ID",R.drawable.dislike_boy);

        ImageView dislikeImageView = findViewById(R.id.dislikeImageView);
        dislikeImageView.setImageResource(gazouNoID);


    }
}
