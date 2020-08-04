package com.example.farata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    private EditText firstName, Email, Phno, Pass, ConfirmPass;
    private Button Submit, Cancel;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        firstName = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        Phno = findViewById(R.id.phonenumber);
        Pass = findViewById(R.id.password);
        ConfirmPass = findViewById(R.id.confirmpassword);
        Submit = findViewById(R.id.submit);
        Cancel = findViewById(R.id.cancel);
        firebaseAuth = FirebaseAuth.getInstance();

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateForm()== true) {
                    firebaseAuth.createUserWithEmailAndPassword(Email.getText().toString(),Pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {

                                firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(),"Email has been sent",Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(),"Email has not been sent.Verify your e-mailid",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Unsuccessful registration",Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                }
            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateForm() {
        int flag = 1;
        if(Phno.getText().toString().length()!=10) {
            Phno.setError("Length must be 10");
            Log.d("SignupActivity", "must be 10");
            flag = 0;
        }
        if(!Pass.getText().toString().equals(ConfirmPass.getText().toString())) {
            ConfirmPass.setError("Password did not match");
            flag = 0;
        }
        String reg = "^[A-Za-z]+$";
        if(!Pattern.compile(reg).matcher(firstName.getText().toString()).matches()) {
            firstName.setError("must contain only letters");
            flag = 0;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(Email.getText().toString()).matches()) {
            Email.setError("Invalid Email");
            flag = 0;
        }

        if(flag == 0)
        {
            return false;
        }
        else {
            return true;
        }
    }
}