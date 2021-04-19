package com.grs21.supervisor.userFragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.grs21.supervisor.R;
import com.grs21.supervisor.adapter.AdapterUserRepairRecyclerView;
import com.grs21.supervisor.databinding.FragmentUserRepairBinding;
import com.grs21.supervisor.model.Repair;
import com.grs21.supervisor.model.User;
import java.util.ArrayList;

public class UserRepairFragment extends Fragment implements View.OnClickListener{
    private FragmentUserRepairBinding binding;
    private User currentUser;
    private Bundle bundle;
    private FirebaseFirestore firebaseFirestore;
    public final String bundleKeyCurrentUser="currentUser";
    private static final String TAG = "UserRepairFragment";
    private ArrayList<Repair> repairArrayList=new ArrayList<>();
    private AdapterUserRepairRecyclerView.UserRepairListener repairListener;
    private RecyclerView repairRecyclerView;
    private Repair clickedRepair;
    private Dialog dialog;
    private TextView editTextDetailBuildName,editTextDetailNote;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentUserRepairBinding.inflate(inflater,container,false);
        bundle=getArguments();
        repairRecyclerView=binding.recyclerViewUserRepair;
        currentUser=(User) bundle.getSerializable(bundleKeyCurrentUser);
        firebaseFirestore=FirebaseFirestore.getInstance();
        getRepairsFromFirebase();
        return binding.getRoot();
    }

    @Override
    public void onClick(View v) {
        dialog.dismiss();
    }

    private void getRepairsFromFirebase() {
        firebaseFirestore.document(currentUser.getCompany()+"/Repairs")
                .collection("repair")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size=queryDocumentSnapshots.getDocuments().size();
                for (int i = 0; i <size ; i++) {
                    String id= queryDocumentSnapshots.getDocuments().get(i).getId();
                    Log.d(TAG, "onSuccess: ");
                    DocumentSnapshot documentSnapshot =queryDocumentSnapshots.getDocuments().get(i);
                    Repair repair= documentSnapshot.toObject(Repair.class);
                    repair.setId(id);
                    repairArrayList.add(repair);
                }
                setAdapter(repairArrayList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }
    private void setAdapter(ArrayList<Repair> repairArrayList) {
        repairListener(repairArrayList);
       AdapterUserRepairRecyclerView adapter=new AdapterUserRepairRecyclerView(repairArrayList, repairListener);
        adapter.notifyDataSetChanged();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        repairRecyclerView.setLayoutManager(manager);
        repairRecyclerView.setAdapter(adapter);
    }
    private void repairListener(ArrayList<Repair> repairArrayList) {
        repairListener =new AdapterUserRepairRecyclerView.UserRepairListener() {
            @Override
            public void onClickListener(View view, int position) {
                clickedRepair=repairArrayList.get(position);
                initializeDetailAlertDialog();
                editTextDetailBuildName.setText(clickedRepair.getApartmentName());
                editTextDetailNote.setText(clickedRepair.getNotes());
            }
        };
    }

    private void initializeDetailAlertDialog() {
        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.alert_dialog_user_repair_detail);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        editTextDetailBuildName=dialog.findViewById(R.id.textViewUserRepairDetailDialogBuildName);
        editTextDetailNote=dialog.findViewById(R.id.textViewUserRepairDetailDialogNote);
        Button buttonCancel=dialog.findViewById(R.id.buttonUserRepairDetailDialogCancel);

        buttonCancel.setOnClickListener(this);

    }



}
