package com.example.todoapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity
{
    EditText et_Name, et_Email, et_Password;
    Button btn_signup;
    TextView link_login;
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_Name = findViewById(R.id.input_name);
        et_Email = findViewById(R.id.input_email);
        et_Password = findViewById(R.id.input_password);
        btn_signup = findViewById(R.id.btn_signup);
        mAuth = FirebaseAuth.getInstance();
        link_login = findViewById(R.id.link_login);

        btn_signup.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                signup();
            }
        });
        
        link_login.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    public void onSignupFailed()
    {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_SHORT).show();

        btn_signup.setEnabled(true);
    }

    public void onSignupSuccess() {
        btn_signup.setEnabled(false);
        setResult(RESULT_OK, null);
        finish();
    }

    private void signup()
    {
        Log.d(TAG, "signup");

        if (!validate())
        {
            onSignupFailed();
            return;
        }

        mAuth.createUserWithEmailAndPassword(et_Email.getText().toString(), et_Password.getText().toString())
            .addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    if (task.isSuccessful())
                    {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null)
                        {
                            user.updateProfile(
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(
                                                    et_Name
                                                        .getText()
                                                        .toString()
                                                    )
                                            .build()
                            );
                        }

                        Toast.makeText(getApplicationContext(), "Authentication Success.",
                                       Toast.LENGTH_SHORT).show();

                        onSignupSuccess();
                    }
                    else
                    {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());

                        Toast.makeText(getApplicationContext(), "Authentication failed user already exists.",
                                       Toast.LENGTH_SHORT).show();

                        onSignupFailed();
                    }
                }
            });
    }

    public boolean validate() {
        boolean valid = true;

        String name = et_Name.getText().toString();
        String email = et_Email.getText().toString();
        String password = et_Password.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            et_Name.setError("at least 3 characters");
            valid = false;
        } else {
            et_Name.setError(null);
        }

        if (email.isEmpty()) {
            et_Email.setError("enter a valid email address");
            valid = false;
        } else
        {
            et_Email.setError(null);
        }

        return valid;
    }
}