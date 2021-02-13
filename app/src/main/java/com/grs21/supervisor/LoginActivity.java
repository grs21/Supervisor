package com.grs21.supervisor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grs21.supervisor.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Boolean state=true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        binding.buttonLogin.setOnClickListener(this);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case   R.id.buttonLogin:
            checkInput(binding.editTextUserName);
            checkInput(binding.editTextPassword);
            if (state) {
                firebaseAuth.signInWithEmailAndPassword(binding.editTextUserName.getText().toString()
                        , binding.editTextPassword.getText().toString()).addOnSuccessListener(authResult -> {

                    Snackbar.make(findViewById(android.R.id.content), "Logged Successfully"
                            , BaseTransientBottomBar.LENGTH_SHORT).show();
                    checkUserAccessLevel(authResult.getUser().getUid());

                }).addOnFailureListener(e -> {
                    Snackbar.make(findViewById(android.R.id.content), e.getMessage()
                            , BaseTransientBottomBar.LENGTH_SHORT).show();

                });
            }
            break;
        }
    }

    private void checkUserAccessLevel(String uid) {
        DocumentReference fr=firebaseFirestore.collection("Users").document(uid);
        fr.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.getString("isAdmin")!=null){
                    startActivity(new Intent(getApplicationContext(),AdminActivity.class));
                    finish();
            }
            if (documentSnapshot.getString("isUser")!=null){
               // startActivity(new Intent(getApplicationContext(),UserActivity.class));

            }
        }).addOnFailureListener(e -> System.out.println(e.getMessage()));
    }


    private void checkInput(EditText editText) {
        if (editText.getText().toString().isEmpty()){
            editText.setError("Error");
            state=false;
        }else {
            state = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),AdminActivity.class));
            finish();
        }
    }
}
