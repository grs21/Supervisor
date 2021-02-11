package com.grs21.supervisor;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grs21.supervisor.databinding.ActivityRegistrationBinding;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityRegistrationBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "RegistrationActivity";
    private Boolean state = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        binding.buttonRegistration.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        checkBoxSetting();

    }

    @Override
    public void onClick(View v) {
        checkInput(binding.editTextUserNameRegistration);
        checkInput(binding.editTextPasswordRegistration);
        checkInput(binding.editTextFullName);
        checkBoxIsCheck();
        if (state&& checkBoxIsCheck()) {
           final String accessLevel;
            if (binding.checkboxUser.isChecked()){
                accessLevel="isUser";
            }else{
                accessLevel="isAdmin";
            }
            firebaseAuth.createUserWithEmailAndPassword(binding.editTextUserNameRegistration.getText().toString()
                    , binding.editTextPasswordRegistration.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Snackbar.make(findViewById(android.R.id.content), "Account Created", BaseTransientBottomBar
                            .LENGTH_LONG).show();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    DocumentReference dr = firebaseFirestore.collection("Users")
                            .document(firebaseUser.getUid());
                    Map<String,Object> userInfo=new HashMap<>();
                    userInfo.put("fullName", binding.editTextFullName.getText().toString());
                    userInfo.put("userName", binding.editTextUserNameRegistration.getText().toString());
                    userInfo.put("password", binding.editTextPasswordRegistration.getText().toString());
                    userInfo.put(accessLevel, "1");
                    dr.set(userInfo);
                    checkUserAccessLevel(authResult.getUser().getUid());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: " + e.getMessage());
                }
            });
        }
    }

    private void checkInput(EditText editText) {
        if (editText.getText().toString().isEmpty()) {
            editText.setError("Error");
            state = false;
        } else {
            state = true;
        }
    }

    private void checkUserAccessLevel(String uid) {

        DocumentReference fr = firebaseFirestore.collection("Users").document(uid);
        fr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "onSuccess: " + documentSnapshot.getData());
                if (documentSnapshot.getString("isAdmin") != null) {
                    // startActivity(new Intent(getApplicationContext(),AdminActivity.class));
                    //finish();
                }
                if (documentSnapshot.getString("isUser") != null) {
                    //startActivity(new Intent(getApplicationContext(),UserActivity.class));
                    //finish();
                }
            }
        });
    }

    private Boolean checkBoxIsCheck() {
        if (!(binding.checkboxUser.isChecked() || binding.checkboxAdmin.isChecked())) {
            Toast.makeText(this, "Select the Account Type", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void checkBoxSetting() {
        binding.checkboxUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxAdmin.setChecked(false);
                }
            }
        });
        binding.checkboxAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.checkboxUser.setChecked(false);
                }
            }
        });
    }
}
