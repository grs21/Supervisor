package com.grs21.supervisor;

import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.grs21.supervisor.databinding.ActivityRegistrationBinding;
import com.grs21.supervisor.model.User;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityRegistrationBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private static final String TAG = "RegistrationActivity";
    private Boolean state = true;
    FirebaseUser firebaseUser;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent=getIntent();
        user=(User)intent.getSerializableExtra("currentUser");
        binding.buttonRegistration.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
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
                accessLevel="user";
            }else{
                accessLevel="admin";
            }
            firebaseAuth.createUserWithEmailAndPassword(binding.editTextUserNameRegistration.getText().toString()
            , binding.editTextPasswordRegistration.getText().toString())
            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                    FirebaseUser createdUser = firebaseAuth.getCurrentUser();
                    String fullName= binding.editTextFullName.getText().toString();
                    String userName=binding.editTextUserNameRegistration.getText().toString();
                    String password=binding.editTextPasswordRegistration.getText().toString();
                    String company=user.getCompany();
                    Map<String,Object> userInfo=new HashMap<>();
                    userInfo.put("fullName",fullName);
                    userInfo.put("userName", userName);
                    userInfo.put("password", password);
                    userInfo.put("accessLevel", accessLevel);
                    userInfo.put("phoneID","1234566789");
                    userInfo.put("company" , company);
                        firebaseFirestore
                        .collection("Users")
                        .document(createdUser.getUid())
                        .set(userInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!++++++++++++++++++");
                        firebaseAuth.signOut();
                        checkUserAccessLevel();
                        }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document+++++++++++++++++++", e);
                            }
                        });
            }}).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.getMessage());
                Toast.makeText(RegistrationActivity.this, e.getMessage()
                        , Toast.LENGTH_SHORT).show();
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

    private void checkUserAccessLevel() {
        if (user.getAccessLevel().equals("admin") ){
            firebaseAuth.signOut();
            firebaseAuth.signInWithEmailAndPassword(user.getUserName(), user.getPassword()).
                addOnSuccessListener(RegistrationActivity.this
                    , new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Intent intent=new Intent(RegistrationActivity.this, AdminActivity.class);
                            intent.putExtra("currentUser",user);
                            startActivity(intent);
                            finish();
                        }
                    });

        }
    }


}
