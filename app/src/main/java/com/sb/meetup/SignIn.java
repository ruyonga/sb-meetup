package com.sb.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class SignIn extends AppCompatActivity {
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login_button)
    Button login_button;
    @BindView(R.id.loading)
    ProgressBar loading;
    @BindView(R.id.create_account)
    TextView create_account;


    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        ButterKnife.bind(this);
        login_button.setOnClickListener(v -> loginUser(username.getText().toString().trim(), password.getText().toString().trim()));
        create_account.setOnClickListener(v -> goSignup());

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

    private void goSignup() {
        startActivity(new Intent(this, RegisterUser.class));

    }

    private void loginUser(String email, String password) {
        loading.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    loading.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        Toasty.success(SignIn.this, "Welcome back"+ user.getDisplayName(),Toast.LENGTH_SHORT).show();

                        switchActivity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG", "signInWithEmail:failure", task.getException());
                        Toasty.error(SignIn.this, "Authentication failed."+task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
