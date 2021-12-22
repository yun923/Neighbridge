package com.example.standing_alone;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class WritePostActivity extends AppCompatActivity {
    private static final String TAG = "WriteActivity";
    private FirebaseAuth mAuth;
    private static final int PICK_IMAGE = 1111;
    private Uri mImageUri;
    private EditText mEditTextFileName;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private String id;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mAuth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        findViewById(R.id.uploadButton).setOnClickListener(onClickListener);
        findViewById(R.id.backstepButton).setOnClickListener(onClickListener);
        findViewById(R.id.insertImageButton).setOnClickListener(onClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            //reload();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            mImageUri = data.getData();

            LinearLayout parent = findViewById(R.id.contentsLayout);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ImageView imageView = new ImageView(WritePostActivity.this);
            imageView.setLayoutParams(layoutParams);
            Glide.with(this).load(mImageUri).override(1000).into(imageView);
            parent.addView(imageView);
        }
    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.uploadButton:
                    profileUpdate();
                    if (mUploadTask != null && mUploadTask.isInProgress()){
                        startToast("upload in progress");
                    }else{
                        uploadFile();
                    }

                    myStartActivity(ViewPostActivity.class);
                    break;
                case R.id.backstepButton:
                    startToast("뒤로가기");
                    finish();
                    break;
                case R.id.insertImageButton:
                    startToast("이미지 삽입하기");
                    //myStartActivity(GalleryActivity.class);
                    pickFromGallery();
                    break;

            }
        }
    };

    private void profileUpdate(){
        final String title = ((EditText)findViewById(R.id.titleText)).getText().toString();
        final String contents = ((EditText)findViewById(R.id.contentText)).getText().toString();
        final String hashtag = ((EditText)findViewById(R.id.hashtagText)).getText().toString();


        if (title.length() > 0 && contents.length() > 0 && hashtag.length() > 0){
            //user = FirebaseAuth.getInstance().getCurrentUser();

            WriteInfo writeInfo = new WriteInfo(title, contents/*, user.getUid()*/,hashtag);
            uploader(writeInfo);
            id = db.collection("posts").document().getId();

            Map<String,Object> post = new HashMap<>();
            post.put("id", id);
            post.put("title", title);
            post.put("Contents", contents);
            post.put("name", hashtag);

        }
    }

    private void uploader(WriteInfo writeInfo){
        db = FirebaseFirestore.getInstance();

        db.collection("posts").add(writeInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        startToast("게시글 등록 완료");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }



    private void startToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
    private void pickFromGallery(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE);

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
    private void uploadFile(){
        if (mImageUri != null){
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
            +"."+getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            startToast("upload successful");
                            mEditTextFileName=(EditText)findViewById(R.id.titleText);

                            Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
                                    taskSnapshot.getStorage().getDownloadUrl().toString());
                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            startToast(e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        }
                    });
        }else{
            startToast("no file selected");
        }
    }

}