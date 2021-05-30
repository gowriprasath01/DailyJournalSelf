package com.example.self;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import Util.JournalApi;
import model.Journal;

public class postJournalActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int IMAGE_CODE = 1;
    private Button saveButton;
    private EditText titleEditText;
    private EditText thoughtEditText;
    private ProgressBar postProgressBar;
    private ImageView addImageView;
    private ImageView imageView;
    private TextView nameTextView;
    private TextView dateTextView;

    private String currentUser;
    private String userId;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private CollectionReference collectionReference= db.collection("Journals");
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);

        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseAuth =FirebaseAuth.getInstance();

        saveButton= findViewById(R.id.post_saveButton);
        titleEditText=findViewById(R.id.post_titleText);
        thoughtEditText=findViewById(R.id.post_thoughts);
        dateTextView=findViewById(R.id.post_dateText);
        nameTextView=findViewById(R.id.post_currentUserText);
        postProgressBar=findViewById(R.id.post_progressBar);
        addImageView=findViewById(R.id.post_add_imageView);
        imageView=findViewById(R.id.post_imageView);

        saveButton.setOnClickListener(this);
        addImageView.setOnClickListener(this);



        if (JournalApi.getInstance() !=null){
            currentUser= JournalApi.getInstance().getUsername();
            userId=JournalApi.getInstance().getUserId();

            nameTextView.setText(currentUser);
        }

        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                user= firebaseAuth.getCurrentUser();
                if(currentUser!= null){
                    //user loggedIn...
                }else{
                    //no users yet...
                }

            }
        };





    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.post_add_imageView:

                Intent intent= new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,IMAGE_CODE);


                break;
            case R.id.post_saveButton:
                saveJournal();
                break;
        }
    }

    private void saveJournal() {

        String title = titleEditText.getText().toString().trim();
        String thought= thoughtEditText.getText().toString().trim();
       postProgressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(title)&&
        !TextUtils.isEmpty(thought) &&
                imageUri !=null)
        {
           final StorageReference filepath = storageReference.child("journal_images")
                    .child("myimage_"+ Timestamp.now().getSeconds());

            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String imageUrl = uri.toString();

                            Journal journal = new Journal();
                            journal.setTitle(title);
                            journal.setThought(thought);
                            // journal.setTimestamp((java.sql.Timestamp) Timestamp.now().toDate());
//                            journal.setTimestamp(new java.sql.Timestamp().now());
                            journal.setImageUrl(imageUrl);
                            currentUser= JournalApi.getInstance().getUsername();
                            userId=JournalApi.getInstance().getUserId();
                            journal.setUserName(currentUser);
                            journal.setUserId(userId);


                            collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                  postProgressBar.setVisibility(View.INVISIBLE);
                                  startActivity(new Intent(postJournalActivity.this,journalListActivity.class));

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Log.d("postJournal", "onFailure: "+e.getMessage());

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }else{

        }



    }

    @Override
    protected void onStart() {
        user= firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
        super.onStart();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMAGE_CODE && resultCode== RESULT_OK){
            if (data !=null){
                imageUri = data.getData();
                imageView.setImageURI(imageUri);

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (user!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}