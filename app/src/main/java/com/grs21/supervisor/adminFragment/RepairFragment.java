package com.grs21.supervisor.adminFragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.grs21.supervisor.R;
import com.grs21.supervisor.adapter.AdapterRepairRecyclerview;
import com.grs21.supervisor.databinding.FragmentRepairBinding;
import com.grs21.supervisor.model.Repair;
import com.grs21.supervisor.model.User;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;


public class RepairFragment extends Fragment implements View.OnClickListener {

    private FragmentRepairBinding binding;
    private EditText editTextBuildName, editTextRepairNote;
    private static final String TAG = "RepairFragment";
    private FirebaseFirestore firebaseFirestore;
    private Dialog dialog;
    private User currentUser;
    private ArrayList<Repair> repairArrayList;
    private RecyclerView repairRecyclerView;
     @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentRepairBinding.inflate(inflater,container,false);
        repairArrayList=new ArrayList<>();
        repairRecyclerView=binding.recyclerViewRepair;
        firebaseFirestore=FirebaseFirestore.getInstance();
        binding.imageButtonRepairAdd.setOnClickListener(this);
        Bundle bundleCurrentUser=getArguments();
        currentUser=(User)bundleCurrentUser.getSerializable("currentUser");
        getRepairsFromFirebase();

        return binding.getRoot();
    }

    private void getRepairsFromFirebase() {

        firebaseFirestore.document(currentUser.getCompany()+"/Repairs")
                .collection("repair")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            repairArrayList= (ArrayList<Repair>) queryDocumentSnapshots.toObjects(Repair.class);
            AdapterRepairRecyclerview adapterRepair= new AdapterRepairRecyclerview(repairArrayList);
            LinearLayoutManager manager = new LinearLayoutManager(getContext());
                repairRecyclerView.setLayoutManager(manager);
                repairRecyclerView.setAdapter(adapterRepair);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageButtonRepairAdd:
                initializeAlertDialog();
                break;
            case R.id.buttonRepairDialogSave:
                ProgressDialog progressDialog=new ProgressDialog(getContext());
                progressDialog.setTitle(R.string.save);
                progressDialog.show();
                String buildName=editTextBuildName.getText().toString();
                String note=editTextRepairNote.getText().toString();
                Repair repair=new Repair(buildName,currentUser,note);
                firebaseFirestore.document(currentUser.getCompany()+"/Repairs")
                        .collection("repair")
                        .add(repair)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                progressDialog.dismiss();
                                Toast toast=Toasty.success(getContext(), R.string.saved);
                                toast.show();
                                dialog.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast toast=Toasty.error(getContext(), R.string.notSaved);
                        toast.show();
                        dialog.dismiss();

                    }
                });

               /* HashMap<String,Object> hashMapRepair=new HashMap<>();
                hashMapRepair.put("buildName", );
                hashMapRepair.put("note", );
                firebaseFirestore.collection(currentUser.getCompany())
                        .document("Repairs")
                         .set(hashMapRepair)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressDialog.dismiss();
                                Toast toast=Toasty.success(getContext(), R.string.saved);
                                toast.show();
                                dialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast toast=Toasty.error(getContext(), R.string.notSaved);
                        toast.show();
                        dialog.dismiss();

                    }
                });*/
                break;
            case R.id.buttonRepairDialogCancel:
                dialog.dismiss();
                break;

        }

    }

    private void initializeAlertDialog() {
         dialog=new Dialog(getContext());
         dialog.setContentView(R.layout.alert_dialog_add_repair);
          editTextBuildName = dialog.findViewById(R.id.editTextRepairDialogBuildName);
          editTextRepairNote =dialog.findViewById(R.id.editTextRepairDialogNote);
          Button buttonSave=dialog.findViewById(R.id.buttonRepairDialogSave);
          Button buttonCancel=dialog.findViewById(R.id.buttonRepairDialogCancel);
          buttonCancel.setOnClickListener(this);
          buttonSave.setOnClickListener(this);
         dialog.show();

    }
}


