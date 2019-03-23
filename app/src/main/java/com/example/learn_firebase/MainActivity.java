package com.example.learn_firebase;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button login;
    private EditText usr, pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        login=findViewById(R.id.button);
        usr=findViewById(R.id.editText);
        pass=findViewById(R.id.editText2);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=usr.getText().toString().trim();
                String pwd=pass.getText().toString().trim();

                if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pwd))
                    Toast.makeText(MainActivity.this, "Fill All Fields!!", Toast.LENGTH_SHORT).show();
                else
                    register(user, pwd);
            }
        });


    }

    public void register(String user, String pwd) {


        mAuth.createUserWithEmailAndPassword(user, pwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                            Toast.makeText(MainActivity.this, "Registered Successfully!!", Toast.LENGTH_SHORT).show();
                        else
                        if (task.getException() instanceof FirebaseAuthWeakPasswordException)
                            Toast.makeText(MainActivity.this, "PassWord Too Weak.\nTry Again!!", Toast.LENGTH_SHORT).show();
                        else if (task.getException() instanceof FirebaseAuthUserCollisionException)
                            Toast.makeText(MainActivity.this, "User Already Exists!! \nTry Again!!", Toast.LENGTH_SHORT).show();
                        else if (task.getException() instanceof FirebaseNetworkException)
                            Toast.makeText(MainActivity.this, "Internet Not Available!\nRetry... ", Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                    }
                });

    }
}
