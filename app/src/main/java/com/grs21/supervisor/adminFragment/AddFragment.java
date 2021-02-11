package com.grs21.supervisor.adminFragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
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

public class AddFragment extends Fragment implements View.OnClickListener, OnConnectionFailedListener {

    private FragmentAddBinding binding;
    private FirebaseFirestore fireStore;
    private static final String TAG = "AddFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentAddBinding.inflate(inflater,container,false);
        binding.buttonDate.setOnClickListener(this);
        binding.buttonBuildSave.setOnClickListener(this);
        binding.buttonBuildDelete.setOnClickListener(this);
        fireStore=FirebaseFirestore.getInstance();

        return binding.getRoot();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonBuildSave:
                final ProgressDialog progressDialog= new ProgressDialog(v.getContext());
                progressDialog.setTitle(R.string.uploading);
                progressDialog.show();
                HashMap<String,Object> buildData=new HashMap<>();
                buildData.put("cost", binding.editTextCost.getText().toString());
                buildData.put("buildName", binding.editTextBuildName.getText().toString());
                buildData.put("address",binding.editTextBuildAddress.getText().toString());
                buildData.put("managerName", binding.editTextManagerName.getText().toString());
                buildData.put("managerNumber", binding.editTextManagerNumber.getText().toString());
                buildData.put("managerAddress", binding.editTextManagerAddress.getText().toString());
                buildData.put("employeeName", binding.editTextEmployeeName.getText().toString());
                buildData.put("employeeNumber", binding.editTextEmployeeNumber.getText().toString());
                buildData.put("dateOfContract", binding.editTextContractDate.getText().toString());

                fireStore.collection("Builds").add(buildData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        progressDialog.dismiss();
                        Toast toastSuccess= Toasty.success(v.getContext(),R.string.saved
                                ,Toast.LENGTH_SHORT,true);
                       toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                       toastSuccess.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                       Toast toastFailure= Toasty.warning(v.getContext(),R.string.notSaved +e.getMessage()
                                ,Toast.LENGTH_SHORT,true);
                       toastFailure.setGravity(Gravity.CENTER, 0, 0);
                       toastFailure.show();
                        Log.d(TAG, "onFailure: "+e.getLocalizedMessage());

                    }
                });

                break;
            case R.id.buttonBuildDelete:

                break;
            case R.id.buttonDate:
                Date date=Calendar.getInstance().getTime();
                String currentDate= DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(date);
                binding.editTextContractDate.setText(currentDate);
              break;

        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed: "+connectionResult.getErrorCode());
    }
}
