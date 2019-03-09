package com.uchinokomoratte.norokoro;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class WaraningyouActivity extends AppCompatActivity {
    int noroiCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waraningyou);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);


        int gazouNoID = prefs.getInt("GAZOU_NO_ID",R.drawable.dislike_boy);

        ImageView dislikeImageView = findViewById(R.id.dislikeImageView);
        dislikeImageView.setImageResource(gazouNoID);
        final TextView countTextView = findViewById(R.id.countTextView);
        ImageView kugibutton = findViewById(R.id.kugi);

        kugibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noroiCount = noroiCount + 1;
                String noroiCountText = String.valueOf(noroiCount);
                countTextView.setText(noroiCountText);
            }
        });




    }
}
