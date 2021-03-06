package com.grs21.supervisor.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.grs21.supervisor.BuildDetailActivity;
import com.grs21.supervisor.R;
import com.grs21.supervisor.databinding.ActivityServiceBinding;
import com.grs21.supervisor.model.Apartment;
import com.grs21.supervisor.model.Service;
import com.grs21.supervisor.model.User;
import com.grs21.supervisor.util.CaptureAct;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class ServiceActivity extends AppCompatActivity implements View.OnClickListener{
    private ActivityServiceBinding binding;
    private Apartment apartment;
    private Intent intent;
    private FirebaseFirestore fireStore;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private static final String TAG = "ServiceActivity";
    private User currentUser;
    private String company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityServiceBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        intent=getIntent();
        apartment=(Apartment) intent.getSerializableExtra("apartment");
        currentUser=(User) intent.getSerializableExtra("currentUser");
        company=currentUser.getCompany();

        Toolbar toolbar=findViewById(R.id.toolbarService);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        binding.textViewServiceToolBarTitle.setText(apartment.getApartmentName());

        firebaseAuth= FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        fireStore=FirebaseFirestore.getInstance();

        binding.buttonServiceScan.setOnClickListener(this);
        binding.buttonServiceSave.setOnClickListener(this);
        binding.textViewServiceBuildName.setText(apartment.getApartmentName());
        binding.imageButtonBackButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageButtonBackButton:
                Intent intent=new Intent(ServiceActivity.this,BuildDetailActivity.class);
                intent.putExtra("apartment",apartment);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
                finish();

                break;
            case R.id.buttonServiceScan:
                scanCode();
                break;
            case  R.id.buttonServiceSave:
                Boolean wellBoolean,elevatorUpBoolean,machineRoomBoolean;
                wellBoolean=binding.checkBoxServiceWell.isChecked();
                elevatorUpBoolean=binding.checkBoxServiceFragmentElevatorUp.isChecked();
                machineRoomBoolean=binding.checkBoxServiceMachineRoom.isChecked();

                if (wellBoolean|| elevatorUpBoolean || machineRoomBoolean){
                    CheckBox DCWell,DCElevatorUp,DCMachineRoom;
                    Dialog dialog=new Dialog(ServiceActivity.this);
                    dialog.setContentView(R.layout.alert_dialog_service);

                    DCElevatorUp=dialog.findViewById(R.id.checkboxServiceDialogElevatorTop);
                    DCMachineRoom=dialog.findViewById(R.id.checkboxServiceDialogElevatorMachine);
                    DCWell=dialog.findViewById(R.id.checkboxServiceDialogWell);
                    DCWell.setChecked(wellBoolean);
                    DCElevatorUp.setChecked(elevatorUpBoolean);
                    DCMachineRoom.setChecked(machineRoomBoolean);

                    TextView textViewName=dialog.findViewById(R.id.textViewServiceDialogApartmentName);
                    textViewName.setText(apartment.getApartmentName());
                    TextView textViewEmployee=dialog.findViewById(R.id.textViewServiceDialogEmployee);
                    textViewEmployee.setText(currentUser.getFullName());

                    Button buttonCancel,buttonSave;
                    buttonCancel=dialog.findViewById(R.id.buttonServiceDialogCancel);
                    buttonSave=dialog.findViewById(R.id.buttonServiceDialogSave);
                    buttonCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    buttonSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final ProgressDialog progressDialog = new ProgressDialog(v.getContext());
                            progressDialog.setTitle(R.string.uploading);
                            progressDialog.show();
                            Date date= Calendar.getInstance().getTime();
                            String dateString= DateFormat.getDateInstance(DateFormat.DATE_FIELD).format(date);
                            Service service=new Service();
                        service.setWell(wellBoolean);
                        service.setMachineRoom(machineRoomBoolean);
                        service.setElevatorUp(elevatorUpBoolean);
                        service.setDate(dateString);
                        service.setEmployee(user.getEmail());
                            Log.d(TAG, "onClick: "+apartment.getUuid());
                            DocumentReference docRef=fireStore.collection(company)
                                    .document(apartment.getUuid());
                            docRef.update("service", FieldValue.arrayUnion(service));
                            dialog.dismiss();
                            progressDialog.dismiss();
                            apartmentGetDataAfterScan();

                        }
                    });
                    dialog.show();
                }else{
                    Toast toastSuccess = Toasty.error(ServiceActivity.this, R.string.please_make_a_service
                            , Toast.LENGTH_LONG, true);
                    toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                    toastSuccess.show();
                }
                break;

        }
    }
    private void scanCode() {
        IntentIntegrator integrator=new IntentIntegrator(ServiceActivity.this );
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning Code");
        integrator.initiateScan();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result!=null){
            if (result.getContents()!=null) {
                try {
                    String well = apartment.getWell();
                    String up = apartment.getElevatorUp();
                    String machineRoom = apartment.getMachineRoom();
                    if (well.equals(result.getContents())) {
                        binding.checkBoxServiceWell.setChecked(true);
                    } else if (up.equals(result.getContents())) {
                        binding.checkBoxServiceFragmentElevatorUp.setChecked(true);
                    } else if (machineRoom.equals(result.getContents())) {
                        binding.checkBoxServiceMachineRoom.setChecked(true);
                    } else {
                        Toast toastSuccess = Toasty.warning(ServiceActivity.this,R.string.not_match
                                , Toast.LENGTH_LONG, true);
                        toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                        toastSuccess.show();
                    }
                } catch (Exception e) {
                    Toast toastSuccess = Toasty.error(ServiceActivity.this, R.string.please_make_a_service
                            , Toast.LENGTH_LONG, true);
                    toastSuccess.setGravity(Gravity.CENTER, 0, 0);
                    toastSuccess.show();
                }
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void apartmentGetDataAfterScan(){
        DocumentReference documentReference=fireStore.collection(company).document(apartment.getUuid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot=task.getResult();
                    if (documentSnapshot.exists()){
                       Map<String,Object> getData=documentSnapshot.getData();
                        Log.d(TAG, "onComplete:+++++++ "+documentSnapshot.getId());
                        Log.d(TAG, "onComplete:+++++++ "+getData.get("buildName"));
                        Apartment apartment=new Apartment(documentSnapshot.getId(),(String)getData.get("buildName")
                                ,(String)getData.get("address"),(String)getData.get("cost"),(String)getData.get("managerName")
                                ,(String)getData.get("managerNumber"),(String)getData.get("managerAddress")
                                ,(String)getData.get("employeeName"),(String)getData.get("employeeNumber")
                                ,(String)getData.get("dateOfContract"),(String) getData.get("wellQRCOdeInfo")
                                ,(String) getData.get("elevatorUpQRCOdeInfo"),(String) getData.get("machineQRCOdeInfo")
                                , (ArrayList<HashMap>) getData.get("service"));
                        Intent intent1=new  Intent(ServiceActivity.this, BuildDetailActivity.class);
                        intent1.putExtra("apartment",apartment);
                        intent1.putExtra("currentUser",currentUser);
                        startActivity(intent1);
                        finish();
                    }
            }
        });
    }
}