package com.grs21.supervisor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
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
import com.grs21.supervisor.activity.AdminActivity;
import com.grs21.supervisor.activity.UserActivity;
import com.grs21.supervisor.databinding.ActivityLoginBinding;
import com.grs21.supervisor.model.User;
import com.grs21.supervisor.util.ToastMessage;
import com.onesignal.OneSignal;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Boolean state=true;
    private ToastMessage toastMessage;
    private ProgressDialog progressDialog;
    private static final String ONESIGNAL_APP_ID = "6e682d9f-2ec1-49a6-ac16-6943f69621c0";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        oneSignalInitialize();
        toastMessage=new ToastMessage();
        binding.buttonLogin.setOnClickListener(this);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

    }

    private void userPhoneIdControl(String userPhoneId,User currentUser) {
        if (!userPhoneId.equals(currentUser.getPhoneID())){
            Log.d(TAG, "userPhoneIdControl: "+firebaseAuth.getCurrentUser().getUid());
            firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                    .update("phoneID",userPhoneId).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(LoginActivity.this, "PhoneID değiştirildi", Toast.LENGTH_LONG).show();
                }
            });
        }else
        {
            Toast.makeText(LoginActivity.this, "PhoneID değiştilmedi", Toast.LENGTH_SHORT).show();
        }
    }
    private void oneSignalInitialize() {
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);
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
                progressDialog.setTitle(R.string.log_in);
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(binding.editTextUserName.getText().toString().trim()
                        , binding.editTextPassword.getText().toString().trim())
                        .addOnSuccessListener(authResult -> {
                            getUserData(authResult.getUser().getUid());
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
                  String phoneID=(String)getData.get("phoneID");
                  User user=new User(fullName,uid,userName,password,accessLevel,company,phoneID);
                  progressDialog.dismiss();
                  checkUserAccessLevel(user);
                  } catch (Exception e){
                      Log.e(TAG, "onComplete: ",e );
                      Snackbar.make(findViewById(android.R.id.content), "NULL"
                          , BaseTransientBottomBar.LENGTH_SHORT).show();
                       Log.d(TAG, "onComplete:+++++++++++++ "+e.getMessage());
                        }
                }else{
                  Log.d(TAG, "onCompleteGetUserData: ++++++++++++++"+task.getException());
                }
            }
        });
    }

    private void checkUserAccessLevel(User user) {
        userPhoneIdControl(OneSignal.getDeviceState().getUserId(), user);
        switch (user.getAccessLevel()){
            case "admin":
                Intent intentAdmin=new Intent(LoginActivity.this, AdminActivity.class);
                intentAdmin.putExtra("currentUser",user);
                startActivity(intentAdmin);
                finish();
                break;
            case "user":
                Intent intentUser=new Intent(LoginActivity.this, UserActivity.class);
                intentUser.putExtra("currentUser",user);
                startActivity(intentUser);
                finish();
                break;
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
            getUserData(FirebaseAuth.getInstance().getCurrentUser().getUid());
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle(R.string.log_in);
            progressDialog.show();
        }
        super.onResume();
    }
}
