package com.grs21.supervisor.adminFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grs21.supervisor.R;
import com.grs21.supervisor.databinding.FragmentAdminAddBinding;
import com.grs21.supervisor.model.Service;
import com.grs21.supervisor.model.User;
import com.grs21.supervisor.util.ToastMessage;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class AddFragment extends Fragment implements View.OnClickListener {

    private FragmentAdminAddBinding binding;
    private FirebaseFirestore fireStore;
    private User currentUser;
    private ToastMessage toastMessage;
    private  String buildName,address,cost,managerName,managerNumber
            ,managerAddress, employeeName,employeeNumber,contractDate;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentAdminAddBinding.inflate(inflater,container,false);
        toastMessage=new ToastMessage();
        binding.buttonDate.setOnClickListener(this);
        binding.buttonBuildSave.setOnClickListener(this);
        fireStore=FirebaseFirestore.getInstance();
        currentUser=(User)getArguments().getSerializable("currentUser");

        return binding.getRoot();
    }


    @Override
    public void onClick(View v) {
        final int save=R.id.buttonBuildSave;
        final int getDate=R.id.buttonDate;
        switch (v.getId()){
            case save:
                getTextValue();
                if (isConnected()) {
                    if (!buildName.isEmpty()) {
                        final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                        progressDialog.setTitle(R.string.uploading);
                        progressDialog.show();
                        HashMap<String, Object> buildData = new HashMap<>();
                        buildData.put("cost", cost);
                        buildData.put("buildName", buildName);
                        buildData.put("address", address);
                        buildData.put("managerName", managerName);
                        buildData.put("managerNumber", managerNumber);
                        buildData.put("managerAddress", managerAddress);
                        buildData.put("employeeName", employeeName);
                        buildData.put("employeeNumber", employeeNumber);
                        buildData.put("dateOfContract", contractDate);
                        buildData.put("wellQRCOdeInfo", buildName + "Well");
                        buildData.put("elevatorUpQRCOdeInfo", buildName+ "ElevatorUp");
                        buildData.put("machineQRCOdeInfo", buildName + "Machine");
                        buildData.put("service", FieldValue.arrayUnion(new Service()));
                        fireStore.collection(currentUser.getCompany())
                                .add(buildData)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                    progressDialog.dismiss();
                                    toastMessage.successMessage(getResources().getString(R.string.saved)
                                            , v.getContext());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                               toastMessage.errorMessage(getResources()
                                       .getString(R.string.notSaved), v.getContext());
                            }
                        });
                        itemReset();
                    } else{
                       toastMessage.warningMessage(getResources()
                               .getString(R.string.write_build_Name), v.getContext());
                    }
                }else{
                    customConnectionDialog();
                }
                break;
            case getDate:
                Date date=Calendar.getInstance().getTime();
                String currentDate= DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(date);
                binding.editTextContractDate.setText(currentDate);
              break;
        }
    }
    private void customConnectionDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext());
        alertDialog.setMessage(R.string.check_connecting).setCancelable(true)
                .setPositiveButton(R.string.connect, (dialog, which)
                        -> startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)))
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.create().show();
    }
    private boolean isConnected() {
        ConnectivityManager connectivityManager=(ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn  =connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }
    private void itemReset(){
        binding.editTextBuildName.setText("");
        binding.editTextBuildAddress.setText("");
        binding.editTextManagerName.setText("");
        binding.editTextManagerNumber.setText("");
        binding.editTextManagerAddress.setText("");
        binding.editTextEmployeeName.setText("");
        binding.editTextEmployeeNumber.setText("");
        binding.editTextContractDate.setText("");
        binding.editTextCost.setText("");
    }
    private void getTextValue(){
        buildName= binding.editTextBuildName.getText().toString().trim();
        cost=binding.editTextCost.getText().toString().trim();
        address=binding.editTextBuildAddress.getText().toString().trim();
        managerName=binding.editTextManagerName.getText().toString().trim();
        managerNumber= binding.editTextManagerNumber.getText().toString().trim();
        managerAddress=binding.editTextManagerAddress.getText().toString().trim();
        employeeName =  binding.editTextEmployeeName.getText().toString().trim();
        employeeNumber=binding.editTextEmployeeNumber.getText().toString().trim();
        contractDate=binding.editTextContractDate.getText().toString().trim();

    }
}
