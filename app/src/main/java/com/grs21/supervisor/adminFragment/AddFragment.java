package com.grs21.supervisor.adminFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grs21.supervisor.R;
import com.grs21.supervisor.databinding.FragmentAddBinding;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import es.dmoral.toasty.Toasty;

public class AddFragment extends Fragment implements View.OnClickListener {

    private FragmentAddBinding binding;
    private FirebaseFirestore fireStore;
    private static final String TAG = "AddFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentAddBinding.inflate(inflater,container,false);
        binding.buttonDate.setOnClickListener(this);
        binding.buttonBuildSave.setOnClickListener(this);
        fireStore=FirebaseFirestore.getInstance();
        return binding.getRoot();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonBuildSave:

                    if (isConnected()) {
                        if (!binding.editTextBuildName.getText().toString().isEmpty()) {
                            final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                            progressDialog.setTitle(R.string.uploading);
                            progressDialog.show();
                            HashMap<String, Object> buildData = new HashMap<>();
                            buildData.put("cost", binding.editTextCost.getText().toString());
                            buildData.put("buildName", binding.editTextBuildName.getText().toString());
                            buildData.put("address", binding.editTextBuildAddress.getText().toString());
                            buildData.put("managerName", binding.editTextManagerName.getText().toString());
                            buildData.put("managerNumber", binding.editTextManagerNumber.getText().toString());
                            buildData.put("managerAddress", binding.editTextManagerAddress.getText().toString());
                            buildData.put("employeeName", binding.editTextEmployeeName.getText().toString());
                            buildData.put("employeeNumber", binding.editTextEmployeeNumber.getText().toString());
                            buildData.put("dateOfContract", binding.editTextContractDate.getText().toString());
                            buildData.put("wellQRCOdeInfo", binding.editTextBuildName.getText().toString() + "Well");
                            buildData.put("elevatorUpQRCOdeInfo", binding.editTextBuildName.getText().toString() + "ElevatorUp");
                            buildData.put("machineQRCOdeInfo", binding.editTextBuildName.getText().toString() + "Machine");
                            fireStore.collection("Builds").add(buildData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressDialog.dismiss();
                                    Toast toastSuccess = Toasty.success(v.getContext(), R.string.saved
                                            , Toast.LENGTH_SHORT, true);
                                    toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                                    toastSuccess.show();
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
                            itemReset();
                        } else{
                            Toast toast=Toasty.warning(getContext(), R.string.write_build_Name
                                    ,Toasty.LENGTH_LONG,true);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }else{
                        customDialog();
                    }
                break;
            case R.id.buttonDate:
                Date date=Calendar.getInstance().getTime();
                String currentDate= DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(date);
                binding.editTextContractDate.setText(currentDate);
              break;

        }
    }
    private void customDialog() {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext());
        alertDialog.setMessage(R.string.check_connecting).setCancelable(true)
                .setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
}
