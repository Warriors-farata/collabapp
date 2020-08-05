package com.example.farata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminUploadedBooks extends AppCompatActivity {

    FirebaseDatabase fb;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_uploaded_books);

        fb = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = fb.getReference().child("BooksPDFs").child(firebaseUser.getUid());
        listView = findViewById(R.id.listview);

        final List<String> bookslist = new ArrayList<>();




        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(snapshot!=null) {
                   for(DataSnapshot i:snapshot.getChildren()) {
                       String bname = i.getValue(BookDetails.class).getBookname();
                       bookslist.add(bname);

                   }

                   ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.bookitem,bookslist){
                       @Override
                       public View getView(int position, View convertView, ViewGroup parent) {
                           View view = super.getView(position, convertView, parent);
                           TextView myText = view.findViewById(R.id.text1);
                           myText.setTextColor(Color.BLACK);

                           return view;
                       }
                   };
                   listView.setAdapter(adapter);


               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {



            }
        });

    }
}