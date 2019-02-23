package com.uchinokomoratte.norokoro;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class SentakuActivity extends AppCompatActivity {

    public void d1(View v){
        ((ImageView) findViewById(R.id.dislike)).setImageResource(R.drawable.dislike1);
    }

    public void d2(View v){
        ((ImageView) findViewById(R.id.dislike)).setImageResource(R.drawable.dislike2);
    }

    public void d3(View v){
        ((ImageView) findViewById(R.id.dislike)).setImageResource(R.drawable.dislike3);
    }

    public void d4(View v){
        ((ImageView) findViewById(R.id.dislike)).setImageResource(R.drawable.dislike4);
    }

    public void d5(View v){
        ((ImageView) findViewById(R.id.dislike)).setImageResource(R.drawable.dislike5);
    }

    public void d6(View v){
        ((ImageView) findViewById(R.id.dislike)).setImageResource(R.drawable.dislike6);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sentaku);
        final EditText nameEditText = findViewById(R.id.nameEditText);
        final ImageView start = findViewById(R.id.start);
        final TextView ErrorText = findViewById(R.id.ErrorText);
        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                if(name.length() == 0){
                    ErrorText.setText("↓なまえいれてよぅ(´･ω･`)ﾈｪﾈｪ" );
                }else {
                    Intent intent = new Intent();
                    intent.setClass(SentakuActivity.this,MainActivity.class);
                    startActivity(intent);
                }
            }
        });
}
}


