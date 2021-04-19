package com.grs21.supervisor.adminFragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.grs21.supervisor.R;
import com.grs21.supervisor.adapter.AdapterRepairRecyclerview;
import com.grs21.supervisor.databinding.FragmentAdminRepairBinding;
import com.grs21.supervisor.model.Repair;
import com.grs21.supervisor.model.User;
import com.grs21.supervisor.util.ToastMessage;
import com.onesignal.OneSignal;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import es.dmoral.toasty.Toasty;

public class RepairFragment extends Fragment implements View.OnClickListener {
    private FragmentAdminRepairBinding binding;
    private EditText editTextAddBuildName, editTextAddRepairNote,editTextDetailBuildName
            ,editTextDetailNote;
    private static final String TAG = "RepairFragment";
    private FirebaseFirestore firebaseFirestore;
    private Dialog dialog;
    private User currentUser;
    private Repair clickedRepair;
    private ArrayList<Repair> repairArrayList=new ArrayList<>();;
    private RecyclerView repairRecyclerView;
    private AdapterRepairRecyclerview.RepairRecyclerviewOnclickListener repairListener;
    private String date;
    private ToastMessage toastMessage;
     @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentAdminRepairBinding.inflate(inflater,container,false);
        setHasOptionsMenu(true);
        toastMessage=new ToastMessage();
        Date currentDate=Calendar.getInstance().getTime();
        date= DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(currentDate);
        repairRecyclerView=binding.recyclerViewRepair;
        repairRecyclerView.setOnClickListener(this);
        firebaseFirestore=FirebaseFirestore.getInstance();
        Bundle bundleCurrentUser=getArguments();
        currentUser=(User)bundleCurrentUser.getSerializable("currentUser");
        getRepairsFromFirebase();
        return binding.getRoot();
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.admin_add_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.menuItemRepairAdd);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                initializeAddAlertDialog();
                return false;
            }
        });
         super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public void onClick(View v) {
         final int detailDialogSave=R.id.buttonRepairDetailDialogSave;
         final int detailDialogDelete=R.id.buttonRepairDetailDialogDelete;
         final int detailDialogCancel=R.id.buttonRepairDetailDialogCancel;
         final int addRepair=R.id.menuItemRepairAdd;
         final int dialogSave=R.id.buttonRepairDialogSave;
         final int dialogCancel=R.id.buttonRepairDialogCancel;
        switch (v.getId()){
            case detailDialogSave:
                String apartmentName= editTextDetailBuildName.getText().toString().trim();
                String repairNote=editTextDetailNote.getText().toString().trim();
                if (!apartmentName.isEmpty() && !repairNote.isEmpty()) {
                    if (isConnected()) {
                        HashMap<String, Object> editData = new HashMap<>();
                        editData.put("apartmentName", apartmentName);
                        editData.put("notes", repairNote);
                        editData.put("date", date);
                        DocumentReference reference = firebaseFirestore.document(currentUser
                                .getCompany() + "/Repairs")
                                .collection("repair")
                                .document(clickedRepair.getId());
                        reference.update(editData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                toastMessage.successMessage(getResources().getString(R.string.saved)
                                        , v.getContext());
                                dialog.dismiss();
                                refreshPage();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                toastMessage.errorMessage(getResources().getString(R.string.notSaved),getContext());
                                Log.e(TAG, "onFailure: ",e );
                            }
                        });
                    }else{
                        customConnectionDialog();
                    }
                }else{
                    toastMessage.warningMessage(getResources().getString(R.string.cannot_be_space)
                            , v.getContext());
                }
                break;
            case detailDialogDelete:
                if (isConnected()) {
                    firebaseFirestore.document(currentUser.getCompany() + "/Repairs")
                            .collection("repair")
                            .document(clickedRepair.getId()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast toast = Toasty.success(getContext(), R.string.deleted);
                            toast.show();
                            dialog.dismiss();
                            refreshPage();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast toast = Toasty.error(getContext(), R.string.not_deleted);
                            toast.show();
                            dialog.dismiss();
                            refreshPage();
                            Log.d(TAG, "onFailure: " + e.getMessage());
                        }
                    });
                }else{
                    customConnectionDialog();
                }
                break;
            case detailDialogCancel:
                dialog.dismiss();
                break;
            case addRepair:
                initializeAddAlertDialog();
                break;
            case dialogSave:
                String buildName= editTextAddBuildName.getText().toString();
                String note= editTextAddRepairNote.getText().toString();
                if (!buildName.isEmpty() && !note.isEmpty()) {
                    if (isConnected()) {
                        ProgressDialog progressDialog=new ProgressDialog(getContext());
                        progressDialog.setTitle(R.string.save);
                        progressDialog.show();
                        Repair repair = new Repair(buildName, currentUser, note, date);
                        firebaseFirestore.document(currentUser.getCompany() + "/Repairs")
                            .collection("repair")
                            .add(repair)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                progressDialog.dismiss();
                                Toast toast = Toasty.success(getContext(), R.string.saved);
                                toast.show();
                                dialog.dismiss();
                                refreshPage();
                                sendNotification(buildName);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast toast = Toasty.error(getContext(), R.string.notSaved);
                                toast.show();
                                dialog.dismiss();
                            }
                        });
                    }else{
                        customConnectionDialog();
                         }
                }else {
                    toastMessage.warningMessage(getResources().getString(R.string.cannot_be_space)
                            , v.getContext());
                }
                break;
            case dialogCancel:
                dialog.dismiss();
                break;
        }
     }

    private void sendNotification(String message){
       firebaseFirestore.collection("Users").get()
         .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
         @Override
         public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
             for (DocumentSnapshot documentSnapshot:queryDocumentSnapshots.getDocuments()){
                 Map<String,Object> getData=documentSnapshot.getData();
                 String company=(String) getData.get("company");
                 String phoneID=(String)getData.get("phoneID");
                 if (currentUser.getCompany().equals(company)){
                     try {
                         OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+"Yeni Arıza!:"+message+"'}" +
                                         ", 'include_player_ids': ['" + phoneID + "']}"),
                         new OneSignal.PostNotificationResponseHandler() {
                             @Override
                             public void onSuccess(JSONObject response) {
                                 Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                                 Toast.makeText(getContext(), "tüm kullanıcılara gönderildi"
                                         , Toast.LENGTH_SHORT).show();
                             }
                             @Override
                             public void onFailure(JSONObject response) {
                                 Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                             }
                         });
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
       });
     }

    private void refreshPage() {
        RepairFragment  repairFragment=new RepairFragment();
        Bundle bundle=new  Bundle();
        bundle.putSerializable("currentUser",currentUser);
        repairFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.admin_fragmentContainer, repairFragment).commit();
    }
    private void initializeAddAlertDialog() {
         dialog=new Dialog(getContext());
         dialog.setContentView(R.layout.alert_dialog_add_repair);
         dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
          editTextAddBuildName = dialog.findViewById(R.id.editTextRepairDialogBuildName);
          editTextAddRepairNote =dialog.findViewById(R.id.editTextRepairDialogNote);
          Button buttonSave=dialog.findViewById(R.id.buttonRepairDialogSave);
          Button buttonCancel=dialog.findViewById(R.id.buttonRepairDialogCancel);
          buttonCancel.setOnClickListener(this);
          buttonSave.setOnClickListener(this);
         dialog.show();
     }
    private void setAdapter(ArrayList<Repair> repairArrayList) {
        repairListener(repairArrayList);
        AdapterRepairRecyclerview adapterRepair= new AdapterRepairRecyclerview(repairArrayList,repairListener);
        adapterRepair.notifyDataSetChanged();
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        repairRecyclerView.setLayoutManager(manager);
        repairRecyclerView.setAdapter(adapterRepair);
    }

    private void repairListener(ArrayList<Repair> repairArrayList) {
        repairListener=new AdapterRepairRecyclerview.RepairRecyclerviewOnclickListener() {
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
        dialog.setContentView(R.layout.alert_dialog_repair_detail);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        editTextDetailBuildName=dialog.findViewById(R.id.editTextRepairDetailDialogBuildName);
        editTextDetailNote=dialog.findViewById(R.id.editTextRepairDetailDialogNote);
        Button buttonSave=dialog.findViewById(R.id.buttonRepairDetailDialogSave);
        Button buttonCancel=dialog.findViewById(R.id.buttonRepairDetailDialogCancel);
        Button buttonDelete=dialog.findViewById(R.id.buttonRepairDetailDialogDelete);
        buttonCancel.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
    }

    private void getRepairsFromFirebase() {
        try {
            firebaseFirestore.document(currentUser.getCompany()+"/Repairs")
                    .collection("repair").orderBy("date", Query.Direction.DESCENDING)
                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    repairArrayList.clear();
                    for (DocumentSnapshot snapshot:queryDocumentSnapshots){
                        Repair repair = snapshot.toObject(Repair.class);
                        repair.setId(snapshot.getId());
                        repairArrayList.add(repair);
                    }
                    setAdapter(repairArrayList);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }catch (Exception e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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
}


