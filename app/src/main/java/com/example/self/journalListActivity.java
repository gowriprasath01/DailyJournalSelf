  package com.example.self;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import Util.JournalApi;
import model.Journal;
import ui.JournalRecyclerview;

public class journalListActivity extends AppCompatActivity {


    private Menu addIcon;
    private Menu signout;

    private TextView noJournal;

    private RecyclerView recyclerView;
    private JournalRecyclerview journalRecyclerviewAdapter;

    private List<Journal> journalList;
    private String userId;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private StorageReference storageReference;
    private FirebaseFirestore db= FirebaseFirestore.getInstance();
    private CollectionReference collectionReference= db.collection("Journals");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);
        journalList=new  ArrayList<>();

        addIcon= findViewById(R.id.add_button);
        signout= findViewById(R.id.signout);
        noJournal=findViewById(R.id.no_journal);

        recyclerView=findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(journalListActivity.this));

        firebaseAuth= FirebaseAuth.getInstance();
        user= firebaseAuth.getCurrentUser();
        //userId= user.getUid();








    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.add_button:

                if (user!=null && firebaseAuth!=null){
                    startActivity(new Intent(journalListActivity.this, postJournalActivity.class));
                }
                //add thoughts by invoking the postJournal activity
                break;
            case R.id.signout:
                if (user!=null && firebaseAuth!=null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(journalListActivity.this, MainActivity.class));
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReference.whereEqualTo("userId", JournalApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot snapshot:queryDocumentSnapshots){
                                Journal journal= snapshot.toObject(Journal.class);
                                journalList.add(journal);
                            }

                        }

                        journalRecyclerviewAdapter= new JournalRecyclerview(journalListActivity.this,journalList);
                        recyclerView.setAdapter(journalRecyclerviewAdapter);
                        journalRecyclerviewAdapter.notifyDataSetChanged();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                noJournal.setVisibility(View.VISIBLE);
            }
        });

    }
}