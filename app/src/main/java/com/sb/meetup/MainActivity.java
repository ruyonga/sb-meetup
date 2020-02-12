package com.sb.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.display_name)
    TextView displayName;
    @BindView(R.id.email)
    TextView email;
    @BindView(R.id.pp)
    ImageView pp;
    @BindView(R.id.logout_btn)
    Button logoutBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();

        ButterKnife.bind(this);

        if (mAuth.getCurrentUser() != null) {
            displayName.setText(mAuth.getCurrentUser().getDisplayName());
            email.setText(mAuth.getCurrentUser().getEmail());
            Glide.with(this)
                        .load(mAuth.getCurrentUser()
                    .getPhotoUrl())
                    .centerCrop().circleCrop()
                    .placeholder(R.mipmap.ic_launcher).into(pp);
        }

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            switchActivity();
        });

    }



    /**
     * Check if user is logged in
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            switchActivity();
        }
    }


    private void switchActivity() {

        startActivity(new Intent(this, SignIn.class));
    }
}
