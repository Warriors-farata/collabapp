package com.example.farata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button loginBtn;
    private EditText Email;
    private EditText Pass;
    private TextView register;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = findViewById(R.id.newuser);
        loginBtn=findViewById(R.id.login);
        Email=findViewById(R.id.email);
        Pass=findViewById(R.id.password);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null) {

                startActivity(new Intent(getApplicationContext(), AdminHome.class));

        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               firebaseAuth.signInWithEmailAndPassword(Email.getText().toString(),Pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()) {
                           if(Email.getText().toString().equals("rajasaibandaru99@gmail.com")) {
                               startActivity(new Intent(getApplicationContext(),AdminHome.class));
                           }
                           else if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                               Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_LONG).show();
                               startActivity(new Intent(getApplicationContext(), Home.class));
                           }
                           else
                           {
                               Toast.makeText(getApplicationContext(), "Please verify your e-mail", Toast.LENGTH_LONG).show();
                           }
                       }
                       else {
                           Toast.makeText(getApplicationContext(),"Login Unsuccessful",Toast.LENGTH_LONG).show();
                       }
                   }
               });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent1);
            }
        });
    }
}