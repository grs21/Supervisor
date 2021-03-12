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
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grs21.supervisor.databinding.ActivityLoginBinding;
import com.grs21.supervisor.model.User;
import com.grs21.supervisor.util.ToastMessage;

import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Boolean state=true;
    private ToastMessage toastMessage;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        toastMessage=new ToastMessage();
        binding.buttonLogin.setOnClickListener(this);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        final int buttonLogin=R.id.buttonLogin;
        switch (v.getId()) {
            case   buttonLogin:
            checkInput(binding.editTextUserName);
            checkInput(binding.editTextPassword);
            if (state) {
                progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setTitle(R.string.uploading);
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(binding.editTextUserName.getText().toString().trim()
                        , binding.editTextPassword.getText().toString().trim())
                        .addOnSuccessListener(authResult -> {
                            Log.d(TAG, "onClick:+++++++ üstloginUserID"+authResult.getUser().getUid());
                            getUserData(authResult.getUser().getUid());
                            Log.d(TAG, "onClick:+++++++ altloginUserID"+authResult.getUser().getUid());

                    Snackbar.make(findViewById(android.R.id.content), "Logged Successfully"
                            , BaseTransientBottomBar.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), e.getMessage()
                            , BaseTransientBottomBar.LENGTH_SHORT).show();
                    Log.d(TAG, "onClick: ++++++++++++++++"+e.getMessage());

                });
            }
            break;
        }
    }
    private void getUserData(String uid){
        DocumentReference userInfo=firebaseFirestore.collection("Users").document(uid);
        userInfo.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                  try {
                  DocumentSnapshot documentSnapshot=task.getResult();
                  Map<String,Object> getData=documentSnapshot.getData();
                  String company=(String) getData.get("company");
                  String fullName=(String) getData.get("fullName");
                  String accessLevel=(String)getData.get("accessLevel");
                  String password=(String)getData.get("password");
                  String userName=(String) getData.get("userName");
                  User user=new User(fullName,uid,userName,password,accessLevel,company);
                  progressDialog.dismiss();
                  checkUserAccessLevel(user);
                  Log.d(TAG, "onComplete: ++++++++++++++"+user.getCompany());

                     } catch (Exception e){
                      Snackbar.make(findViewById(android.R.id.content), "NULL"
                          , BaseTransientBottomBar.LENGTH_SHORT).show();
                       Log.d(TAG, "onComplete:+++++++++++++ "+e.getMessage());
                        }
                }
                else{
                Log.d(TAG, "onCompleteGetUserData: ++++++++++++++"+task.getException());
                    }
            }
        });
    }
    private void checkUserAccessLevel(User user) {
        if (user.getAccessLevel().equals("admin") ){
            Intent intent=new Intent(LoginActivity.this, AdminActivity.class);
            intent.putExtra("currentUser",user);
            startActivity(intent);
            finish();
        } else if (user.getAccessLevel().equals("user")){
            Intent intent=new Intent(LoginActivity.this, UserActivity.class);
            intent.putExtra("currentUser",user);
            startActivity(intent);
            finish();
        }
        else{
            toastMessage.warningMessage("AccessLevel", LoginActivity.this);
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
