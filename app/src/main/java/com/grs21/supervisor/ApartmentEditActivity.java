package com.grs21.supervisor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.SEService;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grs21.supervisor.adminFragment.ApartmentFragment;
import com.grs21.supervisor.databinding.ActivityAppartmentEditBinding;
import com.grs21.supervisor.model.Apartment;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ApartmentEditActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityAppartmentEditBinding binding;
    private Apartment apartment;
    private FirebaseFirestore fireStore;
    private static final String TAG = "ApartmentEditActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAppartmentEditBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        fireStore = FirebaseFirestore.getInstance();
        Intent intent=getIntent();
        apartment=(Apartment) intent.getSerializableExtra("apartment");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(apartment.getApartmentName());
        binding.buttonDetailEditApartmentSave.setOnClickListener(this);
        binding.buttonDetailEditDelete.setOnClickListener(this);
        initializeData(apartment);
    }
    private void initializeData(Apartment apartment) {
        binding.editTextEditBuildName.setText(apartment.getApartmentName());
        binding.editTextEditBuildAddress.setText(apartment.getApartmentAddress());
        binding.editTextEditCost.setText(apartment.getCost());
        binding.editTextEditManagerName.setText(apartment.getManagerName());
        binding.editTextEditManagerNumber.setText(apartment.getManagerNumber());
        binding.editTextEditManagerAddress.setText(apartment.getManagerAddress());
        binding.editTextEditEmployeeName.setText(apartment.getEmployeeName());
        binding.editTextEditEmployeeNumber.setText(apartment.getEmployeeNumber());
        binding.editTextEditContractDate.setText(apartment.getContractDate());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonDetailEditApartmentSave:
            final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
            progressDialog.setTitle(R.string.uploading);
            progressDialog.show();
            DocumentReference apRef = fireStore.collection("Builds").document(apartment.getUuid());
            HashMap<String, Object> editedData = new HashMap<>();
            editedData.put("cost", binding.editTextEditCost.getText().toString());
            editedData.put("buildName", binding.editTextEditBuildName.getText().toString());
            editedData.put("address", binding.editTextEditBuildAddress.getText().toString());
            editedData.put("managerName", binding.editTextEditManagerName.getText().toString());
            editedData.put("managerNumber", binding.editTextEditManagerNumber.getText().toString());
            editedData.put("managerAddress", binding.editTextEditManagerAddress.getText().toString());
            editedData.put("employeeName", binding.editTextEditEmployeeName.getText().toString());
            editedData.put("employeeNumber", binding.editTextEditEmployeeNumber.getText().toString());
            editedData.put("dateOfContract", binding.editTextEditContractDate.getText().toString());
            apRef.update(editedData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    Toast toastSuccess = Toasty.success(v.getContext(), R.string.saved
                            , Toast.LENGTH_SHORT, true);
                    toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                    toastSuccess.show();
                    startActivity(new Intent(ApartmentEditActivity.this, AdminActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast toastFailure = Toasty.warning(v.getContext(), R.string.notSaved + e.getMessage()
                            , Toast.LENGTH_SHORT, true);
                    toastFailure.setGravity(Gravity.CENTER, 0, 0);
                    toastFailure.show();
                }
            });

            break;
            case R.id.buttonDetailEditDelete:

                AlertDialog.Builder alertDialog= new AlertDialog.Builder(ApartmentEditActivity.this);
                alertDialog.setTitle(apartment.getApartmentName()+" Do you want delete !!");

                alertDialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DocumentReference documentReference=fireStore.collection("Builds")
                                .document(apartment.getUuid());
                        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "onComplete: "+task.getException().getLocalizedMessage());
                                if (task.isSuccessful()){
                                    Toast toastSuccess= Toasty.success(v.getContext(),R.string.successfully_delete
                                            ,Toast.LENGTH_SHORT,true);
                                    toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                                    toastSuccess.show();
                                    startActivity(new Intent(ApartmentEditActivity.this, AdminActivity.class));
                                }else {
                                    Log.w(TAG, "HATAAAAA: ",task.getException() );
                                    Toast toastSuccess= Toasty.warning(v.getContext(),R.string.failure_delete
                                            ,Toast.LENGTH_LONG,true);
                                    toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                                    toastSuccess.show();
                                    startActivity(new Intent(ApartmentEditActivity.this, AdminActivity.class));
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "onFailure: ",e.fillInStackTrace() );
                                Toast toastSuccess= Toasty.warning(v.getContext(),R.string.failure_delete
                                        ,Toast.LENGTH_LONG,true);
                                toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                                toastSuccess.show();
                            }
                        });
                    }
                });
               alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {

                   }
               });
                alertDialog.create().show();
               break;
        }
    }



}