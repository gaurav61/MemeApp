package com.example.android.memeapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainActivity extends AppCompatActivity {
    TextView topText;
    TextView bottomText;
    EditText editTop;
    EditText editBottom;
    ImageView imageView;
    private static int RESULT_LOAD_IMAGE =1;



    private boolean shouldAskPermission(){

        return(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topText = (TextView) findViewById(R.id.memeTopText);
        bottomText = (TextView) findViewById(R.id.memeBottomText);
        editTop = (EditText) findViewById(R.id.editTop);
        editBottom =(EditText) findViewById(R.id.editBottom);
        imageView = (ImageView) findViewById(R.id.memeImage);
        if(shouldAskPermission()){
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};

            int permsRequestCode = 200;

            requestPermissions(perms, permsRequestCode);
        }
    }
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:

                boolean writeAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;

                break;

        }

    }
    public void addImage(View view){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
    public void tryMeme(View view){
        topText.setText(editTop.getText().toString());
        bottomText.setText(editBottom.getText().toString());
        hideKeyboard(view);
    }
    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
    }
    public static Bitmap getScreenshot(View view){
        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }
    public void store(Bitmap bm, String filename){
        String dirpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();

        File dir = new File(dirpath);
        if (!dir.exists()){
            dir.mkdir();

        }
        File file = new File(dirpath,filename);

        try {
            FileOutputStream fos = null;
            //Toast.makeText(this,dirpath,Toast.LENGTH_LONG).show();
            fos= new FileOutputStream(file);
            //Toast.makeText(this,dirpath+"jjj",Toast.LENGTH_LONG).show();
            bm.compress(Bitmap.CompressFormat.PNG,100,fos);

            fos.flush();
            fos.close();
            Toast.makeText(this,"Saved",Toast.LENGTH_LONG).show();
        }catch (FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void saveImage(View view){
        View content = findViewById(R.id.lay);
        Bitmap bitmap = getScreenshot(content);
        //Toast.makeText(this,"hmm",Toast.LENGTH_SHORT).show();
        String currentImage = "meme"+ System.currentTimeMillis()+".png";
        store(bitmap,currentImage);
    }
}
