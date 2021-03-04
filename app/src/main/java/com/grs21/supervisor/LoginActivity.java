package com.grs21.supervisor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grs21.supervisor.databinding.ActivityLoginBinding;
import com.grs21.supervisor.model.User;

import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Boolean state=true;
    private ProgressDialog progressDialog;


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
                final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setTitle(R.string.uploading);
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(binding.editTextUserName.getText().toString()
                        , binding.editTextPassword.getText().toString())
                        .addOnSuccessListener(authResult -> {
                            getUserData(authResult.getUser().getUid());
                            progressDialog.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), "Logged Successfully"
                            , BaseTransientBottomBar.LENGTH_SHORT).show();

                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), e.getMessage()
                            , BaseTransientBottomBar.LENGTH_SHORT).show();

                });
            }
            break;
        }
    }
    private void getUserData(String uid){
        Log.d(TAG, "getUserData: "+uid);
        DocumentReference userInfo=firebaseFirestore.collection("Users").document(uid);
        userInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot=task.getResult();
                  try {
                    Log.d(TAG, "getUSerData:++++++++++++++++ "+uid);
                    Map<String,Object> getData=documentSnapshot.getData();
                    String company=(String) getData.get("company");
                    String fullName=(String) getData.get("fullName");
                    String accessLevel=(String)getData.get("accessLevel");
                    String password=(String)getData.get("password");
                    String userName=(String) getData.get("userName");
                    User user=new User(fullName,uid,userName,password,accessLevel,company);
                    checkUserAccessLevel(user);
                    Log.d(TAG, "getUSerData:++++++++++++++++++++++++++++"+fullName);
                    }
                  catch (Exception e){

                      Snackbar.make(findViewById(android.R.id.content), "NULL"
                              , BaseTransientBottomBar.LENGTH_SHORT).show();

                  }
                }
                else{
                    Log.d(TAG, "onCompleteGetUserData: "+task.getException());
                }
            }
        });
    }
    private void checkUserAccessLevel(User user) {
            if (user.getAccessLevel().equals("admin") ){
                Intent intent=new Intent(LoginActivity.this,AdminActivity.class);
                intent.putExtra("currentUser",user);
                   progressDialog.dismiss();
                   startActivity(intent);
                    finish();

            } else if (user.getAccessLevel().equals("user")){
               // startActivity(new Intent(getApplicationContext(),UserActivity.class));

            }

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
        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle(R.string.log_in);
            progressDialog.show();
            getUserData(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }
        super.onResume();
    }
}
