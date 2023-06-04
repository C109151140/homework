package com.example.photo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Button;
import android.view.View;
import android.provider.MediaStore;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;




public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CROP = 3;
    private ImageView imageView;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_REQUEST_READ_FOLDERS = 1001;


    private ActivityResultLauncher<Intent> cropImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap croppedBitmap = extras.getParcelable("data");
                        imageView.setImageBitmap(croppedBitmap);
                    }
                }
            });

    private ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    Intent cropIntent = new Intent("com.android.camera.action.CROP");
                    cropIntent.setDataAndType(imageUri, "image/*");
                    cropIntent.putExtra("crop", "true");
                    cropIntent.putExtra("aspectX", 2);//寬度比例
                    cropIntent.putExtra("aspectY", 1);//高度比例
                    cropIntent.putExtra("outputX", 400);//寬度
                    cropIntent.putExtra("outputY", 200);//高度
                    cropIntent.putExtra("return-data", true);
                    cropImageLauncher.launch(cropIntent);
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] PERMISSIONS = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST_READ_FOLDERS);

        imageView = findViewById(R.id.imageView);

        Button pickImageButton = findViewById(R.id.pickImageButton);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 如果尚未獲取權限，就向使用者請求權限
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageLauncher.launch(pickIntent);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickIntent, REQUEST_IMAGE_PICK);
                } else {

                    Toast.makeText(MainActivity.this, "沒權限", Toast.LENGTH_SHORT).show();
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                    }
                }
                return;
            }
        }
    }

}