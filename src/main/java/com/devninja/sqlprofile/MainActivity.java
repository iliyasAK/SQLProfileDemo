package com.devninja.sqlprofile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int GALLERY_REQ_CODE = 111;
    private final String TAG = getClass().getSimpleName() + " MyLog";
    @BindViews({R.id.edtName, R.id.edtAge, R.id.edtPhone})
    List<TextInputEditText> editTexts;
    @BindView(R.id.imgProf)
    ImageView imgProf;
    @BindViews({R.id.btnAddRec, R.id.btnUpdateRec, R.id.btnListRec})
    List<Button> buttons;
    private int id;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        dbHelper = new DbHelper(this);
        imgProf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageCrop();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getInt("id");
            String name = extras.getString("name");
            String age = extras.getString("age");
            String phone = extras.getString("phone");
            byte[] byteImage = extras.getByteArray("image");

            Bitmap bitmap = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);

            editTexts.get(0).setText(name);
            editTexts.get(1).setText(age);
            editTexts.get(2).setText(phone);
            imgProf.setImageBitmap(bitmap);

            buttons.get(0).setVisibility(View.GONE);
            buttons.get(2).setVisibility(View.GONE);
            buttons.get(1).setVisibility(View.VISIBLE);

        }

    }

    @OnClick({R.id.btnAddRec, R.id.btnUpdateRec, R.id.btnListRec})
    public void BtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddRec:

                saveRecord();

                break;
            case R.id.btnUpdateRec:

                updateRecord();

                break;
            case R.id.btnListRec:

                Log.w(TAG, "Record List Button Clicked !");
                startActivity(new Intent(this, RecordListActivity.class));

                break;
        }

    }

    private void updateRecord() {
        Log.w(TAG, "Update Record Button Clicked !");

        try {
            int row = dbHelper.updateData(
                    editTexts.get(0).getText().toString().trim(),
                    editTexts.get(1).getText().toString().trim(),
                    editTexts.get(2).getText().toString().trim(),
                    imgToByte(imgProf),
                    id
            );
            if (row > 0) {
                Toast.makeText(getApplicationContext(), "Data updated successfully !", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Data update successfull !");
                Intent intentList = new Intent(this, RecordListActivity.class);
                intentList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentList);
            }else{
                Toast.makeText(getApplicationContext(), "Data update unsuccessfull !", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Data update unsuccessfull !");
            }
        } catch (Exception e) {
            Log.w(TAG, e);
        }
    }

    private void imageCrop() {
        Log.w(TAG, "Image Crop Button Clicked !");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    GALLERY_REQ_CODE);
        }
    }

    private void saveRecord() {
        Log.w(TAG, "Save Record Button Clicked !");

        try {
            dbHelper.insertData(
                    editTexts.get(0).getText().toString().trim(),
                    editTexts.get(1).getText().toString().trim(),
                    editTexts.get(2).getText().toString().trim(),
                    imgToByte(imgProf)
            );

            Toast.makeText(getApplicationContext(), "Data inserted successfully !", Toast.LENGTH_LONG).show();

            editTexts.get(0).setText("");
            editTexts.get(1).setText("");
            editTexts.get(2).setText("");
            imgProf.setImageResource(R.drawable.ic_add_photo);

        } catch (Exception e) {
            Log.w(TAG, "" + e);
        }
    }

    private static byte[] imgToByte(ImageView imgProf) {

        Bitmap bitmap = ((BitmapDrawable) imgProf.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == GALLERY_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "File access permission granted !");

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQ_CODE);
            } else {
                Toast.makeText(getApplicationContext(), "Don't have permission to access the location of file !", Toast.LENGTH_LONG).show();
                Log.w(TAG, "File access permission denied !");
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GALLERY_REQ_CODE && resultCode == RESULT_OK) {
            Uri imgUri = data.getData();
            CropImage.activity(imgUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                imgProf.setImageURI(resultUri);
                Log.w(TAG, "Image Cropped Successfully !");
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.w(TAG, error);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
