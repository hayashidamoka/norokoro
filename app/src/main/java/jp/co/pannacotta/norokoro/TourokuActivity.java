package jp.co.pannacotta.norokoro;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import jp.co.pannacotta.norokoro.utill.BitmapUtil;

public class TourokuActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_GALLERY = 1;

    private ImageView dislikeImageView;

    private Uri galleryUri;
    private EditText nameEditText;
    private ImageView startButton;
    private String name;
    private String imagePath;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touroku);

        dislikeImageView = findViewById(R.id.dislike_image_view);
        dislikeImageView.setOnClickListener(this);
        dislikeImageView.setImageResource(R.drawable.dislike_default);
        nameEditText = findViewById(R.id.name_edit_text);
        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString();
                if (name.length() == 0) {
                    Toast.makeText(TourokuActivity.this, getString(R.string.error), Toast.LENGTH_SHORT).show();
                }
                if (galleryUri != null) {
                    // 写真と名前の保存
                    SavePhotoThread thread = new SavePhotoThread();
                    thread.start();
                } else {
                    // 名前の保存
                    saveData();
                    goTourokuActivity();
                    finish();
                }

            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dislike_image_view:
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GALLERY);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            switch (requestCode) {
                case REQUEST_GALLERY:
                    if (data != null) {
                        galleryUri = data.getData();
                        dislikeImageView.setImageURI(galleryUri);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    class SavePhotoThread extends Thread {
        private boolean isHalt;

        SavePhotoThread() {
            isHalt = false;
        }

        public void run() {
            while (!isHalt) {
                ContentResolver cr = getContentResolver();
                Bitmap bitmap = null;
                if (galleryUri != null) {
                    try {
                        bitmap = MediaStore.Images.Media
                                .getBitmap(getContentResolver(), galleryUri);
                    } catch (IOException e) {
                        Toast.makeText(TourokuActivity.this, getString(R.string.add_picture_failed_message), Toast.LENGTH_SHORT).show();
                    } finally {
                        Bitmap resizedBitmap = BitmapUtil.resize(bitmap);
                        long time = System.currentTimeMillis();
                        String name = "norokoro_" + String.valueOf(time) + ".png";
                        String uriPath = MediaStore.Images.Media.insertImage(cr,
                                resizedBitmap, name, null);
                        String[] columns = {MediaStore.Images.Media.DATA};
                        Uri uri = Uri.parse(uriPath);
                        Cursor cursor = cr.query(uri, columns, null, null, null);
                        if (cursor != null) {
                            cursor.moveToFirst();
                            imagePath = cursor.getString(0);
                            cursor.close();
                            isHalt = true;
                            savePhotoHandler.sendEmptyMessage(0);
                        }
                    }
                }
            }
        }

        public void halt() {
            isHalt = true;
            interrupt();
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler savePhotoHandler = new Handler() {
        public void dispatchMessage(Message msg) {
            saveData();
            goTourokuActivity();
            finish();
        }
    };

    private void saveData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TourokuActivity.this);
        prefs.edit().putString("DISLIKE_NAME", name).apply();
        prefs.edit().putString("DISLIKE_IMAGE_PATH", imagePath).apply();
    }

    private void goTourokuActivity() {
        Intent intent = new Intent();
        intent.setClass(TourokuActivity.this, MainActivity.class);
        startActivity(intent);
    }
}


