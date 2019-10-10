package com.sb.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegisterUser extends AppCompatActivity {
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.displayname)
    EditText displayname;
    @BindView(R.id.register_button)
    Button register_button;
    @BindView(R.id.loading)
    ProgressBar loading;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);

        /*
         *
         * Validate the fields
         */
        register_button.setOnClickListener(v -> registerUser(username.getText().toString().trim(), password.getText().toString()));

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            switchActivity();
        }
    }

    private void switchActivity() {

        startActivity(new Intent(this, MainActivity.class));
    }


    private void registerUser(String email, String password) {
        loading.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateFields(user);
                        switchActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterUser.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }

                });
    }

    /**
     * Set extra fields to the profile
     * <p>
     * only limited to name and profile pic
     *
     * @param user
     */

    private void updateFields(FirebaseUser user) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayname.getText().toString().trim())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterUser.this, "Display attached to profile", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
