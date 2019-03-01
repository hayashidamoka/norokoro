package com.uchinokomoratte.norokoro;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ImageView noroubutton = findViewById(R.id.norou_button);
        noroubutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(MainActivity.this,NorouActivity.class);
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

        SharedPreferences prefs =getSharedPreferences("DataSave", Context.MODE_PRIVATE);


        int gazouNoID = prefs.getInt("GAZOU_NO_ID",1);
        String dislike_name = prefs.getString("DISLIKE_NAME","もか");

         ImageView dislikeImageView = findViewById(R.id.dislikeImageView);
         dislikeImageView.setImageResource(gazouNoID);

        TextView nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(dislike_name);

    }
}
