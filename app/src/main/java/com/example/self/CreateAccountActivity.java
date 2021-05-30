package com.example.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Util.JournalApi;

public class CreateAccountActivity extends AppCompatActivity {
    private EditText usernameText;
    private AutoCompleteTextView emailText;
    private EditText passwordText;
    private ProgressBar progressBar;
    private Button createAccountButton;

    //setting up firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //setting up firebase Firestore
    private FirebaseFirestore db= FirebaseFirestore.getInstance() ;

    //path
    private  CollectionReference collectionReference= db.collection("Users");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firebaseAuth= FirebaseAuth.getInstance();

        usernameText= findViewById(R.id.username_create);
        emailText = findViewById(R.id.emailAddress_create);
        passwordText= findViewById(R.id.password_create);
        progressBar= findViewById(R.id.progressBar_create);
        createAccountButton=findViewById(R.id.CreateAcc_Button_create);

        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                currentUser= firebaseAuth.getCurrentUser();
                if(currentUser!= null){
                    //user loggedIn...
                }else{
                    //no users yet...
                }

            }
        };

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!TextUtils.isEmpty(emailText.getText().toString().trim())&& !TextUtils.isEmpty(passwordText.getText().toString().trim())&&
                !TextUtils.isEmpty(usernameText.getText().toString().trim())){

                    String email= emailText.getText().toString().trim();
                    String password= passwordText.getText().toString().trim();
                    String username = usernameText.getText().toString().trim();

                    createAccount(email,password,username);
                }else{
                    Toast.makeText(CreateAccountActivity.this, "Field should not be empty", Toast.LENGTH_SHORT).show();
                }




            }
        });

        }

    private void createAccount(String email, String password, String username) {

        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(username)){

            progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        currentUser= firebaseAuth.getCurrentUser();
                        assert currentUser!=null;
                        String userId = currentUser.getUid();

                        Map<String,String> userObj= new HashMap<>();
                        userObj.put("userId",userId);
                        userObj.put("userName",username);

                        collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {



                                        if (Objects.requireNonNull(task.getResult().exists())){
                                            progressBar.setVisibility(View.INVISIBLE);
                                            String name= task.getResult().getString("userName");

                                            JournalApi journalApi= JournalApi.getInstance();
                                            journalApi.setUserId(userId);
                                            journalApi.setUsername(username);

                                            Toast.makeText(CreateAccountActivity.this, "Account Created", Toast.LENGTH_SHORT).show();

                                           // Log.d("ACTIVITY", "onComplete: "+ "account created");

                                            Intent intent = new Intent(CreateAccountActivity.this,postJournalActivity.class);

                                            intent.putExtra("userName",name);
                                            intent.putExtra("userId", userId);

                                            startActivity(intent);
                                        }

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Log.d("database1", "onFailure: "+ e);

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(CreateAccountActivity.this, "something went wrong!", Toast.LENGTH_SHORT).show();
                                Log.d("database", "onFailure: "+ e.getMessage());
                                progressBar.setVisibility(View.INVISIBLE);

                            }
                        });
                    }

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
        super.onStart();
        currentUser= firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }


    };


