package jp.co.pannacotta.norokoro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import jp.co.pannacotta.norokoro.utill.BitmapUtil;

public class TourokuActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_WRITE_EX_STRAGE_PERMISSION = 0;
    private static final int REQUEST_GALLERY = 1;

    // 登録タイトルのImageViewを定義する
    private ImageView titleImageView;
    private ImageView dislikeImageView;

    private Uri galleryUri;
    private EditText nameEditText;
    private ImageView startButton;
    private String name;
    private String imagePath;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO 北原が作成したレイアウトファイル
        setContentView(R.layout.activity_touroku_kitahara);

        titleImageView = findViewById(R.id.installImageView);
        dislikeImageView = findViewById(R.id.dislike_image_view);
        dislikeImageView.setOnClickListener(this);
        dislikeImageView.setImageResource(R.drawable.dislike_default);
        nameEditText = findViewById(R.id.name_edit_text);
        startButton = findViewById(R.id.startButton);

        // キーボード表示・非表示のイベント設定する
        setKeyboardVisibilityListener(new OnKeyboardVisibilityListener() {
            @Override
            public void onVisibilityChanged(boolean imeVisible) {
                // IMEが表示されている？ titleの画像を消す : titleの画像を表示する
                titleImageView.setVisibility((imeVisible) ? View.GONE : View.VISIBLE);
            }
        });

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

        //もしSDカードの書き込みに許可してもらってなかったらパーミッションをリクエストする
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EX_STRAGE_PERMISSION);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EX_STRAGE_PERMISSION: { //ActivityCompat#requestPermissions()の第2引数で指定した値
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //拒否された時
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setMessage(getString(R.string.permission_message)).setPositiveButton(getString(R.string.permission_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!(ActivityCompat.shouldShowRequestPermissionRationale(TourokuActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                                // permissionをずっと拒否にした時に dialogを送る
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                //Fragmentの時はgetContext().getPackageName()
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            } else {
                                ActivityCompat.requestPermissions(TourokuActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EX_STRAGE_PERMISSION);
                            }
                        }
                    }).setNegativeButton(getString(R.string.app_finish_message), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    dialog.show();
//                    }
                }
                break;
            }
        }
    }

    /**
     * キーボード表示・非表示イベントのリスナー
     */
    private interface OnKeyboardVisibilityListener{
        void onVisibilityChanged(boolean imeVisible);
    }

    /**
     * キーボード表示・非表示のイベントリスナー設定
     */
    private void setKeyboardVisibilityListener(final OnKeyboardVisibilityListener onKeyboardVisibilityListener) {
        // レイアウトの最上位のViewを取得する
        final View parentView = findViewById(R.id.parent_view);

        // 最上位のViewのサイズを取得する
        parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean alreadyOpen;
            // 一般的な IME のサイズ (dp)：ただし経験則
            private final int defaultKeyboardHeightDP = 100;
            //TODO 48dpプラスする理由がわからない・・・
            private final int EstimatedKeyboardDP = defaultKeyboardHeightDP + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 48 : 0);
            private final Rect rect = new Rect();
            @Override
            public void onGlobalLayout() {
                // dpをpxで変更
                int estimatedKeyboardHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, EstimatedKeyboardDP, parentView.getResources().getDisplayMetrics());
                parentView.getWindowVisibleDisplayFrame(rect);
                // 最上位のViewの高さの変更サイズを取得する
                int heightDiff = parentView.getRootView().getHeight() - (rect.bottom - rect.top);

                // 変更サイズが、IMEのサイズ以上であれば、IMEを表示されたことにする
                boolean isShown = heightDiff >= estimatedKeyboardHeight;
                if (isShown == alreadyOpen) {
                    return;
                }
                alreadyOpen = isShown;
                onKeyboardVisibilityListener.onVisibilityChanged(isShown);
            }
        });
    }
}


