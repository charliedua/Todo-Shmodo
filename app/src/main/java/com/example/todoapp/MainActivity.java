package com.example.todoapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private static final int REQUEST_SIGNUP = 0;

    EditText et_Email, et_Password;
    Button btn_LogIn;
    TextView link_SignUp;

    TextView.OnClickListener signUpOnClickListener =
            new TextView.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivityForResult(intent, REQUEST_SIGNUP);
                }
            };

    private FirebaseAuth mAuth;

    private Button.OnClickListener btn_LogIn_OnClickListener
            = new Button.OnClickListener()
    {
        public void onClick(View view)
        {
            new LoginTask().execute();
        }
    };

    public static void RedirectToMain(Context context)
    {
        Intent i = new Intent(context, MainApp.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_Email = findViewById(R.id.et_Email);
        et_Password = findViewById(R.id.et_Password);
        btn_LogIn = findViewById(R.id.btn_LogIn);
        link_SignUp = findViewById(R.id.link_SignUp);
        mAuth = FirebaseAuth.getInstance();

        link_SignUp.setOnClickListener(signUpOnClickListener);
        btn_LogIn.setOnClickListener(btn_LogIn_OnClickListener);
    }

    @Override
    public void onStart()
    {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null)    // Someone is signed in
        {
            Toast.makeText(getApplicationContext(), "Logged in as " + currentUser.getEmail(),
                           Toast.LENGTH_SHORT
                          ).show();
            RedirectToMain(MainActivity.this);
        }
    }

    private class LoginTask extends AsyncTask<Void, Void, Void>
    {
        private String email = "";
        private String password = "";
        private ProgressBar pb_login_load;
        boolean isError = false;

        @Override
        protected void onPreExecute()
        {
            pb_login_load = findViewById(R.id.pb_login_load);
            email = et_Email.getText().toString();
            password = et_Password.getText().toString();

            pb_login_load.setVisibility(View.VISIBLE);

            if (email.isEmpty())
            {
                isError = true;
                et_Email.setError("Empty Emails not accepted");
            }

            if (password.isEmpty())
            {
                isError = true;
                et_Password.setError("Empty Passwords not accepted");
            }
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            // Validation Code


            if (!isError)
            {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                pb_login_load.setVisibility(View.VISIBLE);
                                if (task.isSuccessful())
                                {
                                    Log.d(
                                            TAG,
                                            "signInWithEmailAndPassword:success",
                                            task.getException()
                                         );

                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Successfully logged in!",
                                            Toast.LENGTH_SHORT
                                                  ).show();

                                    RedirectToMain(MainActivity.this);
                                }
                                else
                                {
                                    Log.w(
                                            TAG,
                                            "signInWithEmailAndPassword:failure",
                                            task.getException()
                                         );

                                    et_Email.setError("Email Incorrect");
                                    et_Password.setError("Password Incorrect");

                                    Toast.makeText(
                                            getApplicationContext(),
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT
                                                  ).show();
                                }
                            }
                        });
            }
            return null;
        }
    }
}
