package com.grs21.supervisor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grs21.supervisor.activity.AdminActivity;
import com.grs21.supervisor.activity.ServiceActivity;
import com.grs21.supervisor.databinding.ActivityAdminBuildDetailBinding;
import com.grs21.supervisor.model.Apartment;
import com.grs21.supervisor.model.Service;
import com.grs21.supervisor.model.User;
import com.grs21.supervisor.util.ToastMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import android.graphics.Bitmap;
import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class    AdminBuildDetailActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener {
    private ActivityAdminBuildDetailBinding binding;
    private Intent intent;
    private Bitmap bitmap;
    private Apartment apartment;
    private Dialog spinnerDialog,qrDialog;
    private FirebaseFirestore fireStore;
    private ArrayList<Service> serviceArrayList=new ArrayList<>();
    private ArrayList<File> saveImageFile=new ArrayList<>();
    private EditText editTextMailForQRCOde;
    private static final String TAG = "AdminBuildDetailActivity";
    private ToastMessage toastMessage=new ToastMessage();
    private ArrayList<Uri>uriArrayList=new ArrayList<>();
    private User currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAdminBuildDetailBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        fireStore=FirebaseFirestore.getInstance();
        binding.buttonDetailToEdit.setOnClickListener(this);
        binding.buttonMakeService.setOnClickListener(this);
        binding.autoCompleteSpinnerAdmin.setOnItemClickListener(this);
        intent=getIntent();
        apartment=(Apartment) intent.getSerializableExtra("apartment");
        currentUser=(User)intent.getSerializableExtra("currentUser");
        initializeToolbar();
        initializeData(apartment);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.admin_qrgenerator, menu);
        return true;
    }
    private void initializeToolbar(){
        Toolbar toolbar=binding.toolbarBuildDetail;
        toolbar.setTitle(apartment.getApartmentName());
        setSupportActionBar(toolbar);
    }
    private void initializeData(Apartment apartment) {
        for (HashMap service:apartment.getServiceArrayList()) {
            Service generateService=new Service(
                    (boolean)service.get("well")
                    ,(boolean)service.get("elevatorUp")
                    ,(boolean)service.get("machineRoom")
                    ,(String) service.get("date")
                    ,(String) service.get("employee")
                    ,(String)service.get("cost"));
            serviceArrayList.add(generateService);
        }
        ArrayAdapter<Service> adapter = new ArrayAdapter<>(this,
                R.layout.item_spinner, serviceArrayList);
        binding.autoCompleteSpinnerAdmin.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        binding.textViewBuildDetailBuildName.setText(apartment.getApartmentName());
        binding.textViewBuildDetailBuildAddress.setText(apartment.getApartmentAddress());
        binding.textViewBuildDetailCost.setText(apartment.getCost());
        binding.textViewBuildDetailManagerName.setText(apartment.getManagerName());
        binding.textViewBuildDetailManagerNumber.setText(apartment.getManagerNumber());
        binding.textViewBuildDetailManagerAddress.setText(apartment.getManagerAddress());
        binding.textViewBuildDetailEmployeeName.setText(apartment.getEmployeeName());
        binding.textViewBuildDetailEmployeeNumber.setText(apartment.getEmployeeNumber());
        binding.textViewBuildDetailContractDate.setText(apartment.getContractDate());
    }
    @Override
    public void onClick(View v) {
        final int buttonGotoEdit=R.id.buttonDetailToEdit;
        final int buttonServiceDetailDialogCancel=R.id.buttonAdminDetailDialogCancel;
        final int buttonMakeService=R.id.buttonMakeService;
        final int buttonAlertDialogSend=R.id.buttonAlertDialogQRCodeGenerate;
        switch (v.getId()){
            case buttonGotoEdit:
                Intent intent=new Intent(AdminBuildDetailActivity.this, ApartmentEditActivity.class);
                intent.putExtra("apartment", (Serializable) apartment);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
                finish();
                break;
            case buttonServiceDetailDialogCancel:
                spinnerDialog.dismiss();
                break;
            case buttonMakeService:
                Intent intent1=new Intent(AdminBuildDetailActivity.this, ServiceActivity.class);
                intent1.putExtra("apartment", apartment);
                intent1.putExtra("currentUser",currentUser);
                startActivity(intent1);
                finish();
                break;
            case buttonAlertDialogSend:
             if (editTextMailForQRCOde.getText().toString().isEmpty()){
                 toastMessage.warningMessage("Lütfen bir mail adresi giriniz", this);
             }else{
                        saveImageFile.clear();
                        uriArrayList.clear();
                        ArrayList<String> qrCodes = apartment.getBuildQrCodes();
                     if (ContextCompat.checkSelfPermission(AdminBuildDetailActivity.this
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AdminBuildDetailActivity.this
                        , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1 );
                     }else{
                         for (String qrValue : qrCodes) {
                             saveImage( qrValue);
                         }
                         for (File file:saveImageFile) {
                         Uri uri= FileProvider.getUriForFile(this, "com.grs21.supervisor", file);
                         uriArrayList.add(uri);
                         }
                         sendMail(editTextMailForQRCOde.getText().toString(),uriArrayList);
                         qrDialog.dismiss();
                     }
             }
                break;
        }
    }
    private void  deleteImage(ArrayList<File> uriArrayList){
        for (File file :uriArrayList) {
            file.delete();
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int qrCodeGenerate=R.id.menuItemQrGenerate;
        if (item.getItemId() == qrCodeGenerate) {
            qrDialog=new Dialog(this);
            qrDialog.setContentView(R.layout.alert_dialog_enter_mail);
            qrDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            qrDialog.show();
            Button buttonGenerate=qrDialog.findViewById(R.id.buttonAlertDialogQRCodeGenerate);
            buttonGenerate.setOnClickListener(this);
            editTextMailForQRCOde=qrDialog.findViewById(R.id.editTextAlertDialogQRCode);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void sendMail(String mailAddress, ArrayList<Uri> uris){
        Intent mailIntent=new Intent(Intent.ACTION_SEND_MULTIPLE);
        mailIntent.setType("plain/text");
        mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailAddress});
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, apartment.getApartmentName()+" QR KOD");
        mailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,uris);
        startActivity(mailIntent);
    }
    private void saveImage( String fileName){
        try {
            QRGEncoder qrGenerator=new QRGEncoder(fileName,null,QRGContents.Type.TEXT,350);
            bitmap=qrGenerator.getBitmap();
            File filePath= Environment.getExternalStorageDirectory();
            File parentPath=new File(filePath.getAbsolutePath()+"/Supervisor");
            parentPath.mkdir();
            File file=new File(parentPath,fileName+".jpeg");
            FileOutputStream outputStream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            saveImageFile.add(file);
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            Log.e("+++++++", "imageSave: ",e );
        }
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(AdminBuildDetailActivity.this, AdminActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("currentUser", currentUser);
        startActivity(intent);
        super.onBackPressed();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==1){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                   ArrayList<String> qrCodes = apartment.getBuildQrCodes();
                if (ContextCompat.checkSelfPermission(AdminBuildDetailActivity.this
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(AdminBuildDetailActivity.this
                            , new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1 );
                }else{
                    for (String qrValue : qrCodes) {
                        saveImage( qrValue);
                    }
                    for (File file:saveImageFile) {
                        Uri uri= FileProvider.getUriForFile(this, "com.grs21.supervisor", file);
                        uriArrayList.add(uri);

                    }
                    sendMail(editTextMailForQRCOde.getText().toString(),uriArrayList);
                    qrDialog.dismiss();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    @Override
    protected void onResume() {
        super.onResume();
        deleteImage(saveImageFile);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView textViewDialogDate,textViewDialogEmployee,textViewDialogCost;
        CheckBox checkBoxWell,checkBoxUp,checkBoxMachineRoom;

        Service spinnerService = serviceArrayList.get(position);
        spinnerDialog = new Dialog(AdminBuildDetailActivity.this);
        spinnerDialog.setContentView(R.layout.alert_dialog_admin_service_detail);
        spinnerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        textViewDialogDate = spinnerDialog.findViewById(R.id.textViewAdminDetailDialogDate);
        textViewDialogEmployee = spinnerDialog.findViewById(R.id.textViewAdminDetailDialogEmployee);
        textViewDialogCost= spinnerDialog.findViewById(R.id.textViewAdminDetailDialogCost);

        checkBoxMachineRoom = spinnerDialog.findViewById(R.id.checkboxAdminDetailDialogElevatorMachine);
        checkBoxUp = spinnerDialog.findViewById(R.id.checkboxAdminDetailDialogElevatorTop);
        checkBoxWell = spinnerDialog.findViewById(R.id.checkboxAdminDetailDialogWell);

        checkBoxWell.setChecked(spinnerService.getWell());
        checkBoxUp.setChecked(spinnerService.getElevatorUp());
        checkBoxMachineRoom.setChecked(spinnerService.getMachineRoom());

        textViewDialogEmployee.setText(spinnerService.getEmployee());
        textViewDialogDate.setText(spinnerService.getDate());
        textViewDialogCost.setText(spinnerService.getCost());
        spinnerDialog.findViewById(R.id.buttonAdminDetailDialogCancel).setOnClickListener(this);
        spinnerDialog.findViewById(R.id.buttonAdminDetailAdminDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminBuildDetailActivity.this);
                alertDialog.setTitle(getResources().getString(R.string.do_you_want_delete));
                alertDialog.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        serviceArrayList.remove(position);
                        DocumentReference apRef = fireStore.collection(currentUser.getCompany()).document(apartment.getUuid());
                        apRef.update("service",serviceArrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    toastMessage.successMessage("Silindi ", AdminBuildDetailActivity.this);
                                    spinnerDialog.dismiss();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                toastMessage.errorMessage("Silme başarısız", AdminBuildDetailActivity.this);
                                Log.e("+++++++++++++++", "onFailure: ",e );
                            }
                        });
                    }
                });
                alertDialog.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        spinnerDialog.dismiss();
                    }
                });
                alertDialog.create().show();
            }
        });
        spinnerDialog.show();
    }
}