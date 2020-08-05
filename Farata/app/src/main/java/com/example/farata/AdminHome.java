package com.example.farata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AdminHome extends AppCompatActivity {

    public AdminHome() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    private BookDetails bookDetails;
    private Button UploadBook;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase fb;
    private DatabaseReference databaseReference;
    private DatabaseReference dbrf;
    private Button ViewBooks;
    private EditText Book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        UploadBook =findViewById(R.id.uploadbook);
        ViewBooks = findViewById(R.id.viewbooks);
        Book = findViewById(R.id.book);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        fb = FirebaseDatabase.getInstance();
        databaseReference = fb.getReference();



        dbrf = databaseReference.child("BooksPDFs").child(firebaseUser.getUid());

        UploadBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent,9);
            }
        });

        ViewBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AdminUploadedBooks.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading....");

        if(requestCode == 9 && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                String path = data.getData().getLastPathSegment();
                final String bookname = path.substring(path.lastIndexOf('/')+1);
                progressDialog.show();
                storageReference.child(firebaseUser.getUid()+"/"+bookname).putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()) {

                            Toast.makeText(getApplicationContext(),"Uploaded",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Uploading Failed",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());
                        Uri url = uri.getResult();
                         bookDetails = new BookDetails(bookname,url.toString());
                         dbrf.addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                 float ccount = snapshot.getChildrenCount()+1;
                                 func(ccount);
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError error) {

                             }
                         });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded"+(int)progress+"%");

                    }
                });
            }
        }
    }

    private void func(float ccount) {
        dbrf.child("book"+(int)ccount).setValue(bookDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"Details Uploaded",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),task.getException().toString(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logoutbutton:
                FirebaseAuth.getInstance().signOut();
                //firebaseUser = null;
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}