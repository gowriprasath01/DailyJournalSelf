package com.example.self;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import Util.JournalApi;

public class MainActivity extends AppCompatActivity {
private Button startButton;

private FirebaseAuth firebaseAuth;
private FirebaseAuth.AuthStateListener authStateListener;
private FirebaseUser currentUser;

private FirebaseFirestore db= FirebaseFirestore.getInstance();
CollectionReference collectionReference= db.collection("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setElevation(0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startButton =findViewById(R.id.start_Button);

        firebaseAuth= FirebaseAuth.getInstance();
        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
               currentUser=firebaseAuth.getCurrentUser();
               if(currentUser !=null){
                   currentUser=firebaseAuth.getCurrentUser();
                   String currentUserId = currentUser.getUid();

                   collectionReference.
                           whereEqualTo("userId",currentUserId)
                           .addSnapshotListener(new EventListener<QuerySnapshot>() {
                               @Override
                               public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                  if(error != null){

                                  }

                                  String name;

                                  if(!value.isEmpty()){
                                      for(QueryDocumentSnapshot snapshot : value){
                                          JournalApi journalApi = JournalApi.getInstance();
                                          journalApi.setUserId(snapshot.getString("userId"));
                                          journalApi.setUsername(snapshot.getString("userName"));

                                          startActivity(new Intent(MainActivity.this,journalListActivity.class));
                                          finish();
                                      }
                                  }
                                  }
                           });
               }
            }
        };

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,Login_Activity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth!=null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}