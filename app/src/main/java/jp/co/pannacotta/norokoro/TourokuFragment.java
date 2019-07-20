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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

import jp.co.pannacotta.norokoro.utill.BitmapUtil;

public class TourokuFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_WRITE_EX_STRAGE_PERMISSION = 0;
    private static final int REQUEST_GALLERY = 1;

    private ImageView dislikeImageView;

    private Uri galleryUri;
    private EditText nameEditText;
    private ImageView startButton;
    private String name;
    private String imagePath;

    private static NorokoroMediaPlayer player = NorokoroMediaPlayer.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_touroku, container);
        dislikeImageView = view.findViewById(R.id.dislike_image_view);
        dislikeImageView.setOnClickListener(this);
        dislikeImageView.setImageResource(R.drawable.dislike_default);
        nameEditText = view.findViewById(R.id.name_edit_text);
        startButton = view.findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEditText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getActivity(), getString(R.string.error), Toast.LENGTH_SHORT).show();
                } else {
                    if (galleryUri != null) {
                        // 写真と名前の保存
                        SavePhotoThread thread = new SavePhotoThread();
                        thread.start();
                    } else {
                        // 名前の保存
                        saveData();
                        goTourokuActivity();
                        requireActivity().finish();
                    }
                }
            }

        });

        //もしSDカードの書き込みに許可してもらってなかったらパーミッションをリクエストする
        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EX_STRAGE_PERMISSION);
        }
        return view;
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
                        Toast.makeText(TourokuFragment.this, getString(R.string.add_picture_failed_message), Toast.LENGTH_SHORT).show();
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
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TourokuFragment.this);
        prefs.edit().putString("DISLIKE_NAME", name).apply();
        prefs.edit().putString("DISLIKE_IMAGE_PATH", imagePath).apply();
    }

    private void goTourokuActivity() {
        Intent intent = new Intent();
        intent.setClass(TourokuFragment.this, ChooseFragment.class);
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
                            if (!(ActivityCompat.shouldShowRequestPermissionRationale(TourokuFragment.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                                // permissionをずっと拒否にした時に dialogを送る
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                //Fragmentの時はgetContext().getPackageName()
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            } else {
                                ActivityCompat.requestPermissions(TourokuFragment.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EX_STRAGE_PERMISSION);
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

    public void playSound(View view){
        if(player.mp == null)
            player.mp = MediaPlayer.create(getApplicationContext(), R.raw.mainsong);
            player.mp.start();
    }
}

