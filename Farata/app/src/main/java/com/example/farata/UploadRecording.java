package com.example.farata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UploadRecording extends AppCompatActivity {

    private String bname,url;
    private TextView textView;
    private Button Download,Upload;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference dbrf;
    FirebaseUser firebaseUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_recording);

        bname = getIntent().getStringExtra("BookName");
        url = getIntent().getStringExtra("URL");
        textView = findViewById(R.id.bookname_tv);
        Download = findViewById(R.id.download);
        Upload = findViewById(R.id.upload);
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = firebaseStorage.getReference().child("recordings/");
        databaseReference = FirebaseDatabase.getInstance().getReference();
        dbrf = databaseReference.child("recordings").child(firebaseUser.getUid());

        textView.setText(url    );

        Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                Toast.makeText(getApplicationContext(),"link toast",Toast.LENGTH_LONG).show();
                startActivity(intent);
            }
        });

        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent, 8);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading....");

        if(requestCode == 8 && resultCode == RESULT_OK) {
            if(data != null && data.getData() != null) {
                Toast.makeText(getApplicationContext(),"hi",Toast.LENGTH_LONG).show();
                storeRecording(data.getData());
            }

        }
    }

    private void storeRecording(Uri data) {
        progressDialog.show();
        Toast.makeText(getApplicationContext(),"hello",Toast.LENGTH_LONG).show();
        storageReference.child(bname).putFile(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"Recording Uploaded",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext()," Recording Uploading Failed",Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        })
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri url = uri.getResult();
                BookDetails bookDetails = new BookDetails(bname,url.toString());
                dbrf.child("angular").setValue(bookDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Recording Details Uploaded",Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Recording Details Not Uploaded",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded"+(int)progress+"%");
            }
        });

    }
}