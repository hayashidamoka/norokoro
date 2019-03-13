package jp.co.pannacotta.norokoro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class TourokuActivity extends AppCompatActivity {
    private int gazouNoID = R.drawable.dislike_boy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touroku);
        final EditText nameEditText = findViewById(R.id.nameEditText);
        final ImageView start = findViewById(R.id.start);
        final TextView ErrorText = findViewById(R.id.errorText);
        final ImageView dislike = findViewById(R.id.dislike);

        ImageView boyImageView = findViewById(R.id.dislike_boy);
        boyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislike.setImageResource(R.drawable.dislike_boy);
                gazouNoID = R.drawable.dislike_boy;
            }
        });

        ImageView girlImageView = findViewById(R.id.dislike_girl);
        girlImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislike.setImageResource(R.drawable.dislike_girl);
                gazouNoID = R.drawable.dislike_girl;
            }
        });

        ImageView bareImageView = findViewById(R.id.dislike_bare);
        bareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislike.setImageResource(R.drawable.dislike_bare);
                gazouNoID = R.drawable.dislike_bare;
            }
        });

        ImageView misterImageView = findViewById(R.id.dislike_mister);
        misterImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislike.setImageResource(R.drawable.dislike_mister);
                gazouNoID = R.drawable.dislike_mister;
            }
        });

        ImageView ladyImageView = findViewById(R.id.dislike_lady);
        ladyImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislike.setImageResource(R.drawable.dislike_lady);
                gazouNoID = R.drawable.dislike_lady;
            }
        });

        ImageView devilImageView = findViewById(R.id.dislike_devil);
        devilImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislike.setImageResource(R.drawable.dislike_devil);
                gazouNoID = R.drawable.dislike_devil;
            }
        });

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                if(name.length() == 0){
                    ErrorText.setText(getString(R.string.error));
                }else {
                    // 画像IDを保存
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TourokuActivity.this);
                    prefs.edit().putInt("GAZOU_NO_ID",gazouNoID).apply();
                    prefs.edit().putString("DISLIKE_NAME",name).apply();
                    Intent intent = new Intent();
                    intent.setClass(TourokuActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }

        });
}

}


