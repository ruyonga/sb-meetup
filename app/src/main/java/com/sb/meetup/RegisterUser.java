package com.sb.meetup;

import android.content.Intent;
import android.net.Uri;
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
import es.dmoral.toasty.Toasty;

public class RegisterUser extends AppCompatActivity {
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.displayname)
    EditText displayname;
    @BindView(R.id.register_button)
    Button registerButton;
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
        registerButton.setOnClickListener(v -> registerUser(username.getText().toString().trim(), password.getText().toString()));

    }


    /**
     * Check if the current user is null before loading the register user Activity
     * Null= not signed in
     * NotNull = signed user.
     */

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
                        assert user != null;
                        updateFields(user);

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterUser.this, "Account Registration failed.."+task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();

                    }

                });
    }

    /**
     * Set extra fields to the profile
     * <p>
     * only limited to name and profile pic
     *
     * Hard coded Image
     *
     * @param user
     */

    private void updateFields(FirebaseUser user) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayname.getText().toString().trim())
                .setPhotoUri(Uri.parse("https://www.gstatic.com/mobilesdk/180227_mobilesdk/database_rules_zerostate.png"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        switchActivity();
                        Toasty.success(RegisterUser.this, "Registration successful", Toast.LENGTH_LONG).show();

                        Log.d("TAG", "updateFields:success");
                    }else{
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterUser.this, "Account Registration failed.."+task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }


}
