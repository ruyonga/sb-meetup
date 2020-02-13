package com.sb.meetup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sb.meetup.models.UserModal;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.display_name)
    TextView displayName;

    @BindView(R.id.email)
    TextView email;

    @BindView(R.id.pp)
    ImageView pp;

    @BindView(R.id.logout_btn)
    Button logoutBtn;

    @BindView(R.id.update)
    Button update;


    @BindView(R.id.username)
    TextView username;
    @BindView(R.id.phonenumber)
    TextView phonenumber;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.bio)
    TextView bio;

    FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        if (mAuth.getCurrentUser() != null) {
            displayName.setText(mAuth.getCurrentUser().getDisplayName());
            email.setText(mAuth.getCurrentUser().getEmail());
            Glide.with(this)
                    .load(mAuth.getCurrentUser()
                            .getPhotoUrl())
                    .centerCrop().circleCrop()
                    .placeholder(R.mipmap.ic_launcher).into(pp);
            moreInfo();
        }

        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            switchActivity();
        });

        update.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ProfileAccount.class)));

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
            Toasty.warning(MainActivity.this, "Please login to continue", Toast.LENGTH_LONG).show();
            switchActivity();
        }
    }


    private void switchActivity() {

        startActivity(new Intent(this, SignIn.class));
    }

    private void moreInfo() {
        db.collection("users").document(mAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    UserModal user = documentSnapshot.toObject(UserModal.class);

                    assert user != null;
                    username.setText(user.getUsername());
                    bio.setText(user.getBio());
                    address.setText(user.getAddress());
                    phonenumber.setText(user.getContact());
                    Log.d(getClass().getSimpleName(), user.getAddress());
                }
            }
        });


    }
}
