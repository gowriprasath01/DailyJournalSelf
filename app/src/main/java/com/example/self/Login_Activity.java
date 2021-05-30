package com.example.self;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import Util.JournalApi;

public class Login_Activity extends AppCompatActivity {
    private Button loginButton;
    private Button createButton;

    private AutoCompleteTextView email_login;
    private EditText password_login;



    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference collectionReference= db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        firebaseAuth= FirebaseAuth.getInstance();

        loginButton= findViewById(R.id.loginButton);
        createButton=findViewById(R.id.CreateAcc_Button);
        email_login= findViewById(R.id.emailAddress);
        password_login= findViewById(R.id.password);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email= email_login.getText().toString().trim();
                String password = password_login.getText().toString().trim();

                loginEmailPasswordUser(email,password);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Activity.this,CreateAccountActivity.class));
            }
        });
    }

    private void loginEmailPasswordUser(String email, String password) {

        if (!TextUtils.isEmpty(email)
        && !TextUtils.isEmpty(password)){

            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                  FirebaseUser user = firebaseAuth.getCurrentUser();
                  assert user!=null;
                  String CurrentUserId= user.getUid();

                  collectionReference.whereEqualTo("userId",CurrentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                      @Override
                      public void onEvent(@Nullable QuerySnapshot value,
                                          @Nullable FirebaseFirestoreException error) {
                          if(error!=null){

                          }
                          assert  value!=null;
                          if(!value.isEmpty()){

                              for (QueryDocumentSnapshot snapshot: value){
                                  JournalApi journalApi= JournalApi.getInstance();
                                  journalApi.setUsername(snapshot.getString("userName"));
                                  journalApi.setUserId(snapshot.getString("userId"));

                                  startActivity(new Intent(Login_Activity.this,postJournalActivity.class));

                              }


                          }



                      }
                  });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }else{
            Toast.makeText(this, "Enter the email and password", Toast.LENGTH_SHORT).show();
        }

    }
}