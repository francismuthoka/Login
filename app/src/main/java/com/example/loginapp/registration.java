package com.example.loginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class registration extends AppCompatActivity {
    TextInputEditText email,password,fname,sname,phoneno,idno;
    Button login,register_btn;
    FirebaseDatabase db;
    FirebaseAuth auth;
    DatabaseReference dbreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        fname=findViewById(R.id.fname);
        sname=findViewById(R.id.sname);
        phoneno=findViewById(R.id.phone);
        idno=findViewById(R.id.idno);
        login=findViewById(R.id.login_button);
        register_btn=findViewById(R.id.register_btn);
        db=FirebaseDatabase.getInstance();
        dbreference=db.getReference();
        auth=FirebaseAuth.getInstance();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(registration.this,MainActivity.class);
                startActivity(intent);
            }
        });
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               registerUser();
            }
        });
    }
    private void registerUser()
    {
        String emailText=email.getText().toString().trim();
        String passwordText=password.getText().toString().trim();
        String fnameText=fname.getText().toString().trim();
        String snameText=sname.getText().toString().trim();
        String phonenoText=phoneno.getText().toString().trim();
        String idnoText=idno.getText().toString().trim();
        if(fnameText.isEmpty())
        {
            fname.setError("this field cant be empty");
            fname.requestFocus();
            return;
        }
        if(snameText.isEmpty())
        {
            sname.setError("this field cant be empty");
            sname.requestFocus();
            return;
        }
        if(!Patterns.PHONE.matcher(phonenoText).matches())
        {
            phoneno.setError("enter valid phone number");
            phoneno.requestFocus();
            return;
        }
        else if (idnoText.isEmpty()||idnoText.length()<7||idnoText.length()>8)
        {
            idno.setError("enter valid id no");
            idno.requestFocus();
            return;
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches())
        {
            email.setError("please enter valid email");
            email.requestFocus();
            return;
        }
        else if(true!=passwordValidates(passwordText))
        {
            password.setError("please enter strong password");
            password.requestFocus();
            return;
        }
        else{

            auth.createUserWithEmailAndPassword(emailText,passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        String id=auth.getCurrentUser().getUid().toString();
                        dbreference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.hasChild(id))
                                {
                                    Toast.makeText(getApplicationContext(),"this email has been used",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    dbreference.child("users").child(id).child("email").setValue(emailText);
                                    dbreference.child("users").child(id).child("password").setValue(passwordText);
                                    dbreference.child("users").child(id).child("first name").setValue(fnameText);
                                    dbreference.child("users").child(id).child("last name").setValue(snameText);
                                    dbreference.child("users").child(id).child("phone number").setValue(phonenoText);
                                    dbreference.child("users").child(id).child("id number").setValue(idnoText);

                                    Toast.makeText(getApplicationContext(),"successifully registered", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(registration.this,MainActivity.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getApplicationContext(),"something went wrong",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                }
            });

        }

    }
    private boolean passwordValidates( String pass ) {
        int count = 0;
        if( 8 <= pass.length() && pass.length() <= 32  )
        {
            if(pass.matches(".*\\d.*"))
                count ++;
            if(pass.matches(".*[a-z].*"))
                count ++;
            if(pass.matches(".*[A-Z].*"))
                count ++;
        }
        return count>=3;
    }
}