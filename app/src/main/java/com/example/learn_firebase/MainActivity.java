package com.example.learn_firebase;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    ImageView imageHolder;
    private final int requestCode = 20;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageHolder = findViewById(R.id.imageView);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        findViewById(R.id.re).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file=new File(currentPhotoPath);
                file.delete();
                dispatchTakePictureIntent();
            }
        });

        findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, Translate.class);
                intent.putExtra("path", currentPhotoPath);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(this.requestCode == requestCode && resultCode == RESULT_OK){
            setPic();
            findViewById(R.id.re).setVisibility(View.VISIBLE);
            findViewById(R.id.go).setVisibility(View.VISIBLE);
            findViewById(R.id.msg).setVisibility(View.VISIBLE);
            findViewById(R.id.button).setVisibility(View.GONE);
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.learn_firebase", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, requestCode);
            }
        }
    }

    private File createImageFile() throws IOException {
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("temp",".jpg", storageDir);

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageHolder.getWidth();
        int targetH = imageHolder.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageHolder.setImageBitmap(bitmap);

    }

}
