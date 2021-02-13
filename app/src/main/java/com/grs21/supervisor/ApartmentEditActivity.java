package com.grs21.supervisor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grs21.supervisor.databinding.ActivityAppartmentEditBinding;
import com.grs21.supervisor.model.Apartment;
import java.util.HashMap;
import es.dmoral.toasty.Toasty;

public class ApartmentEditActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityAppartmentEditBinding binding;
    private Apartment apartment;
    private FirebaseFirestore fireStore;
    private static final String TAG = "ApartmentEditActivity";
    private  String toastMessageFailureDelete;
    private  String toastMessageSuccessfullyDelete;
    private  String alertDialogDoYouWantDelete;
    private  String toastMessageNotSaved;
    private  String toastMessageSaved;
    private  String alertDialogCancel;
    private  String alertDialogConnect;
    private  String alertDialogDelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAppartmentEditBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        Toolbar toolbar=binding.toolBarEditActivity;
        fireStore = FirebaseFirestore.getInstance();
        Intent intent=getIntent();
        apartment=(Apartment) intent.getSerializableExtra("apartment");

        toastMessageFailureDelete=getResources().getString(R.string.failure_delete);
        toastMessageSuccessfullyDelete=getResources().getString(R.string.successfully_delete);
        alertDialogDoYouWantDelete=getResources().getString(R.string.do_you_want_delete);
        toastMessageNotSaved=getResources().getString(R.string.notSaved);
        toastMessageSaved=getResources().getString(R.string.saved);
        alertDialogCancel=getResources().getString(R.string.cancel);
        alertDialogConnect=getResources().getString(R.string.connect);
        alertDialogDelete=getResources().getString(R.string.delete);
        setSupportActionBar(toolbar);
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
                    Toast toastSuccess = Toasty.success(v.getContext(), toastMessageSaved
                            , Toast.LENGTH_SHORT, true);
                    toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                    toastSuccess.show();
                    startActivity(new Intent(ApartmentEditActivity.this, AdminActivity.class));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast toastFailure = Toasty.warning(v.getContext(), toastMessageNotSaved+ e.getMessage()
                            , Toast.LENGTH_SHORT, true);
                    toastFailure.setGravity(Gravity.CENTER, 0, 0);
                    toastFailure.show();
                }
            });

            break;
            case R.id.buttonDetailEditDelete:
                if (!isConnected()){
                    customDialog();
                }
                if (isConnected()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ApartmentEditActivity.this);
                    alertDialog.setTitle(apartment.getApartmentName() + alertDialogDoYouWantDelete);
                    alertDialog.setPositiveButton(alertDialogDelete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DocumentReference documentReference = fireStore.collection("Builds")
                                    .document(apartment.getUuid());
                            documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast toastSuccess = Toasty.success(v.getContext(), toastMessageSuccessfullyDelete
                                                , Toast.LENGTH_SHORT, true);
                                        toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                                        toastSuccess.show();
                                        startActivity(new Intent(ApartmentEditActivity.this, AdminActivity.class));
                                    } else {
                                        Toast toastSuccess = Toasty.warning(v.getContext(), toastMessageFailureDelete
                                                , Toast.LENGTH_LONG, true);
                                        toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                                        toastSuccess.show();
                                        startActivity(new Intent(ApartmentEditActivity.this, AdminActivity.class));
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast toastSuccess = Toasty.warning(v.getContext(), toastMessageFailureDelete
                                            , Toast.LENGTH_LONG, true);
                                    toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                                    toastSuccess.show();
                                }
                            });
                        }
                    });
                    alertDialog.setNegativeButton(alertDialogCancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alertDialog.create().show();
                }
               break;
        }
    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager=(ConnectivityManager)getApplicationContext().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn  =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }
    private void customDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(ApartmentEditActivity.this);
        alertDialog.setMessage("Please connect to internet to proceed further").setCancelable(true)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.create().show();
    }


}
