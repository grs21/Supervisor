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
import com.grs21.supervisor.model.User;
import com.grs21.supervisor.util.ToastMessage;

import java.util.HashMap;
import es.dmoral.toasty.Toasty;

public class ApartmentEditActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityAppartmentEditBinding binding;
    private Apartment apartment;
    private User currentUSer;
    private FirebaseFirestore fireStore;
    private static final String TAG = "ApartmentEditActivity";
    private  String toastMessageFailureDelete,toastMessageSuccessfullyDelete,alertDialogDoYouWantDelete
            ,toastMessageNotSaved,toastMessageSaved,alertDialogCancel,alertDialogConnect,alertDialogDelete;
    private  String buildName,address,cost,managerName,managerNumber
            ,managerAddress, employeeName,employeeNumber,contractDate;
    private ToastMessage toastMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAppartmentEditBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        toastMessage=new ToastMessage();
        getIntentData();
        initializeToolBar();
        setMessageData();

        binding.buttonDetailEditApartmentSave.setOnClickListener(this);
        binding.buttonDetailEditDelete.setOnClickListener(this);
        binding.imageButtonEditToDetailBackButton.setOnClickListener(this);
        initializeData(apartment);
    }



    @Override
    public void onClick(View v) {

        final int buttonDetailEditApartmentSave = R.id.buttonDetailEditApartmentSave;
        final int buttonDetailEditDelete=R.id.buttonDetailEditDelete;
        final int imageButtonEditToDetailBackButton=R.id.imageButtonEditToDetailBackButton;
        switch (v.getId()) {

            case buttonDetailEditApartmentSave:
                if (!isConnected()){
                    customDialog();
                }
            if (isConnected()) {
                final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                progressDialog.setTitle(R.string.uploading);
                progressDialog.show();
                getTextValue();
                updatedApartment();
                DocumentReference apRef = fireStore.collection(currentUSer.getCompany()).document(apartment.getUuid());
                HashMap<String, Object> editedData = new HashMap<>();
                editedData.put("cost", cost);
                editedData.put("buildName", buildName);
                editedData.put("address", address);
                editedData.put("managerName", managerName);
                editedData.put("managerNumber", managerNumber);
                editedData.put("managerAddress", managerAddress);
                editedData.put("employeeName", employeeName);
                editedData.put("employeeNumber", employeeNumber);
                editedData.put("dateOfContract", contractDate);
                apRef.update(editedData).addOnSuccessListener(new OnSuccessListener<Void>()  {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        toastMessage.successMessage(toastMessageSaved, v.getContext());
                        startDetailActivity();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast toastFailure = Toasty.warning(v.getContext(), toastMessageNotSaved + e.getMessage()
                                , Toast.LENGTH_SHORT, true);
                        toastFailure.setGravity(Gravity.CENTER, 0, 0);
                        toastFailure.show();
                    }
                });
            }
            break;
            case buttonDetailEditDelete:
                if (!isConnected()){
                    customDialog();
                }
                if (isConnected()) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ApartmentEditActivity.this);
                    alertDialog.setTitle(apartment.getApartmentName() + alertDialogDoYouWantDelete);
                    alertDialog.setPositiveButton(alertDialogDelete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        DocumentReference documentReference = fireStore.collection(currentUSer.getCompany())
                                .document(apartment.getUuid());
                        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                 toastMessage.successMessage(toastMessageSuccessfullyDelete, v.getContext());
                                 startAdminActivity();
                                    finish();

                                } else {
                                    toastMessage.warningMessage(toastMessageFailureDelete, v.getContext());
                                }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                  toastMessage.warningMessage(toastMessageFailureDelete, v.getContext());
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
            case imageButtonEditToDetailBackButton:
                startDetailActivity();
                finish();
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

    private void startDetailActivity(){
        Intent intent=new Intent(ApartmentEditActivity.this,BuildDetailActivity.class);
        intent.putExtra("currentUser", currentUSer);
        intent.putExtra("apartment", apartment);
        startActivity(intent);
    }

    private void startAdminActivity(){
        Intent intent=new Intent(ApartmentEditActivity.this,AdminActivity.class);
        intent.putExtra("currentUser", currentUSer);
        startActivity(intent);
    }

    private void updatedApartment(){

        apartment.setApartmentName(buildName);
        apartment.setApartmentAddress(address);
        apartment.setCost(cost);
        apartment.setEmployeeName(employeeName);
        apartment.setEmployeeNumber(employeeNumber);
        apartment.setManagerName(managerName);
        apartment.setManagerAddress(managerAddress);
        apartment.setManagerNumber(managerNumber);
        apartment.setContractDate(contractDate);

    }

    private void getTextValue(){
        buildName= binding.editTextEditBuildName.getText().toString().trim();
        cost=binding.editTextEditCost.getText().toString().trim();
        address=binding.editTextEditBuildAddress.getText().toString().trim();
        managerName=binding.editTextEditManagerName.getText().toString().trim();
        managerNumber= binding.editTextEditManagerNumber.getText().toString().trim();
        managerAddress=binding.editTextEditManagerAddress.getText().toString().trim();
        employeeName =  binding.editTextEditEmployeeName.getText().toString().trim();
        employeeNumber=binding.editTextEditEmployeeNumber.getText().toString().trim();
        contractDate=binding.editTextEditContractDate.getText().toString().trim();

    }

    private void setMessageData(){
        toastMessageFailureDelete=getResources().getString(R.string.failure_delete);
        toastMessageSuccessfullyDelete=getResources().getString(R.string.successfully_delete);
        alertDialogDoYouWantDelete=getResources().getString(R.string.do_you_want_delete);
        toastMessageNotSaved=getResources().getString(R.string.notSaved);
        toastMessageSaved=getResources().getString(R.string.saved);
        alertDialogCancel=getResources().getString(R.string.cancel);
        alertDialogConnect=getResources().getString(R.string.connect);
        alertDialogDelete=getResources().getString(R.string.delete);
    }

    private void initializeToolBar(){
        Toolbar toolbar=binding.toolBarEditActivity;
        fireStore = FirebaseFirestore.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        binding.textViewEditToolBarTitle.setText(apartment.getApartmentName());
    }

    private void getIntentData() {
        Intent intent=getIntent();
        apartment=(Apartment) intent.getSerializableExtra("apartment");
        currentUSer=(User)intent.getSerializableExtra("currentUser");
    }


}
