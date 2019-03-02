package com.wangdiam.studybuddycapstoneproject.ui.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wangdiam.studybuddycapstoneproject.R;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {


    // UI references.
    private FirebaseAuth mAuth;
    @BindView
   (R.id.email) AutoCompleteTextView mEmailView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.password_reenter) EditText mPasswordRetypeView;
    @BindView(R.id.name) EditText mNameView;
    @BindView(R.id.register_button) Button registerButton;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        // Set up the login form.
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                String retype = mPasswordRetypeView.getText().toString();
                String name = mNameView.getText().toString();
                Log.d("EMAIL", email);
                Log.d("PASSWORD",password);
                Log.d("RETYPE",retype);
                if (password.equals(retype) && !password.equals("") && !email.equals("") && !name.equals("")) {
                    pd = new ProgressDialog(RegisterActivity.this);
                    pd.setTitle("Registering User");
                    pd.setMessage("Please wait while we create your account");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();
                    registerUser(email, password, retype, name);
                } else if (email.equals("")) {
                    Toast.makeText(RegisterActivity.this,"Registration failed: Please enter your email", Toast.LENGTH_SHORT).show();
                } else if (password.length() < 4) {
                    Toast.makeText(RegisterActivity.this, "Registration failed: Please enter a longer password", Toast.LENGTH_SHORT).show();
                } else if (name.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Registration failed: Please enter your name", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this,"Registration failed: You have re-entered a different password", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void registerUser(final String email, String password, String retype, final String name) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            pd.dismiss();
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference().child("users").child(uid);
                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("displayName",name);
                            userMap.put("Email",email);
                            myRef.setValue(userMap);
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build();
                            currentUser.updateProfile(profileUpdates);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SUCCESSFUL", "createUserWithEmail:success");
                            Intent intent = new Intent(RegisterActivity.this, LandingActivity.class);
                            intent.putExtra("NAME",name);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            pd.hide();
                            // If sign in fails, display a message to the user.
                            Log.w("FAILED", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

