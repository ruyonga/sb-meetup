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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.sb.meetup.models.UserModal;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class ProfileAccount extends AppCompatActivity {

    FirebaseFirestore db;
    UserModal userModal;
    @BindView(R.id.username)
    EditText username;
    @BindView(R.id.phonenumber)
    EditText phonenumber;
    @BindView(R.id.address)
    EditText address;
    @BindView(R.id.bio)
    EditText bio;

    @BindView(R.id.loading)
    ProgressBar progressBar;

    @BindView(R.id.update_profile)
    Button updateProfile;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_account);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        userModal = new UserModal();
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        moreInfo();

        updateProfile.setOnClickListener(v -> saveToFireStore());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            switchActivity();
        }
    }

    /**
     * SetOptions.merge()
     * Will add the data to an exisiting document if it already exist
     */

    private void saveToFireStore() {

        userModal.setBio(bio.getText().toString());
        userModal.setAddress(address.getText().toString());
        userModal.setContact(phonenumber.getText().toString());
        userModal.setUsername(username.getText().toString());


        db.collection("users").document(mAuth.getCurrentUser().getUid()).set(userModal, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toasty.success(ProfileAccount.this, "Profile update Successful", Toast.LENGTH_LONG).show();

                    startActivity(new Intent(ProfileAccount.this, MainActivity.class));
                }).addOnFailureListener(e -> Toasty.error(ProfileAccount.this, "Error loading profile info...", Toast.LENGTH_LONG).show());


    }

    private void switchActivity() {

        startActivity(new Intent(this, SignIn.class));
    }

    private void moreInfo() {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        progressBar.setVisibility(View.GONE);

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
                }).addOnFailureListener(e -> Toasty.error(ProfileAccount.this, "Error loading profile info...", Toast.LENGTH_LONG).show());


    }
}
