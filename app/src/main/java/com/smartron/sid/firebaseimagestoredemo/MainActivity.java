package com.smartron.sid.firebaseimagestoredemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_IMAGE_CAPTURE = 111;
    Button capture_image, retrive_image;
    private static ImageView image_capture,image_capture1;
    private ImagePojo imagePojo;
    String imageEncoded;
    private String userId;
    DatabaseReference ref;
    FirebaseUser currentFirebaseUser;
    private FirebaseDatabase mFirebaseInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        capture_image = (Button) findViewById(R.id.capture_image);
        retrive_image = (Button) findViewById(R.id.retrive_image);
        image_capture = (ImageView) findViewById(R.id.image_capture);
        image_capture1 = (ImageView) findViewById(R.id.image_capture1);
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //userId = currentFirebaseUser.getUid();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        capture_image.setOnClickListener(this);
        retrive_image.setOnClickListener(this);
        imagePojo=new ImagePojo();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            image_capture.setImageBitmap(imageBitmap);
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] byteArray = baos.toByteArray();
         imageEncoded = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        System.out.print("Image"+imageEncoded);
        Log.d("Img",imageEncoded);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("imageURL");
        ref.setValue(imageEncoded);
    }

   /* public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {

        byte[]   imageBytes = Base64.decode(image, Base64.NO_WRAP);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.capture_image:
                onLaunchCamera();

                break;
           case R.id.retrive_image:
                Bitmap image = null;

               mFirebaseInstance.getReference("imageURL").addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       Log.e("TAG", "App title updated");

                       String appTitle = dataSnapshot.getValue(String.class);
                       // update toolbar title
                      // getSupportActionBar().setTitle(appTitle);
                       byte[] imageBytes = Base64.decode(appTitle, Base64.NO_WRAP);
                       Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                       image_capture1.setImageBitmap(decodedImage);
                   }

                   @Override
                   public void onCancelled(DatabaseError error) {
                       // Failed to read value
                       Log.e("TAG", "Failed to read app title value.", error.toException());
                   }
               });

        break;
        }
    }



}
