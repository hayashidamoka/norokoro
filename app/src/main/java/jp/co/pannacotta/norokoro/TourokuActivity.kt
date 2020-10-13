package jp.co.pannacotta.norokoro

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.preference.PreferenceManager
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import jp.co.pannacotta.norokoro.utill.BitmapUtil
import java.io.IOException

class TourokuActivity : AppCompatActivity(), View.OnClickListener {

    private var dislikeImageView: ImageView? = null
    private var galleryUri: Uri? = null
    private var nameEditText: EditText? = null
    private var startButton: ImageView? = null
    private var name: String? = null
    private var imagePath: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_touroku)

        dislikeImageView = findViewById(R.id.dislike_image_view)
        dislikeImageView!!.setOnClickListener(this)
        dislikeImageView!!.setImageResource(R.drawable.dislike_default)

        nameEditText = findViewById(R.id.name_edit_text)

        startButton = findViewById(R.id.startButton)
        startButton!!.setOnClickListener(View.OnClickListener {
            name = nameEditText!!.getText().toString()
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(this@TourokuActivity, getString(R.string.error), Toast.LENGTH_SHORT).show()
            } else {
                if (galleryUri != null) {
                    // 写真と名前の保存
                    val thread = SavePhotoThread()
                    thread.start()
                } else {
                    // 名前の保存
                    saveData()
                    goTourokuActivity()
                    finish()
                }
            }
        })

        //もしSDカードの書き込みに許可してもらってなかったらパーミッションをリクエストする
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_EX_STRAGE_PERMISSION)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.dislike_image_view -> {
                val intent = Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, REQUEST_GALLERY)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = false
            when (requestCode) {
                REQUEST_GALLERY -> if (data != null) {
                    galleryUri = data.data
                    dislikeImageView!!.setImageURI(galleryUri)
                }
                else -> {
                }
            }
        }
    }

    internal inner class SavePhotoThread : Thread() {
        private var isHalt = false
        override fun run() {
            while (!isHalt) {
                val cr = contentResolver
                var bitmap: Bitmap? = null
                if (galleryUri != null) {
                    try {
                        val source = ImageDecoder.createSource(cr, galleryUri!!)
                        bitmap = ImageDecoder.decodeBitmap(source)
                    } catch (e: IOException) {
                        Toast.makeText(this@TourokuActivity, getString(R.string.add_picture_failed_message), Toast.LENGTH_SHORT).show()
                    } finally {
                        val resizedBitmap = BitmapUtil.resize(bitmap)
                        val time = System.currentTimeMillis()
                        val name = "norokoro_$time.png"
                        val uriPath = MediaStore.Images.Media.insertImage(cr,
                                resizedBitmap, name, null)
                        val columns = arrayOf(MediaStore.Images.Media.DATA)
                        val uri = Uri.parse(uriPath)
                        val cursor = cr.query(uri, columns, null, null, null)
                        if (cursor != null) {
                            cursor.moveToFirst()
                            imagePath = cursor.getString(0)
                            cursor.close()
                            isHalt = true
                            savePhotoHandler.sendEmptyMessage(0)
                        }
                    }
                }
            }
        }

        fun halt() {
            isHalt = true
            interrupt()
        }

    }

    @SuppressLint("HandlerLeak")
    private val savePhotoHandler: Handler = object : Handler() {
        override fun dispatchMessage(msg: Message) {
            saveData()
            goTourokuActivity()
            finish()
        }
    }

    private fun saveData() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this@TourokuActivity)
        prefs.edit().putString("DISLIKE_NAME", name).apply()
        prefs.edit().putString("DISLIKE_IMAGE_PATH", imagePath).apply()
    }

    private fun goTourokuActivity() {
        val intent = Intent()
        intent.setClass(this@TourokuActivity, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_WRITE_EX_STRAGE_PERMISSION -> {
                //ActivityCompat#requestPermissions()の第2引数で指定した値
                if (grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //拒否された時
                    val dialog = AlertDialog.Builder(this)
                    dialog.setMessage(getString(R.string.permission_message)).setPositiveButton(getString(R.string.permission_yes)) { dialog, which ->
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this@TourokuActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            // permissionをずっと拒否にした時に dialogを送る
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            //Fragmentの時はgetContext().getPackageName()
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        } else {
                            ActivityCompat.requestPermissions(this@TourokuActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_WRITE_EX_STRAGE_PERMISSION)
                        }
                    }.setNegativeButton(getString(R.string.app_finish_message)) { dialog, which -> finish() }
                    dialog.show()
                    //                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_WRITE_EX_STRAGE_PERMISSION = 0
        private const val REQUEST_GALLERY = 1
    }
}