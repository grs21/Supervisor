package com.grs21.supervisor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
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
import android.widget.Spinner;
import android.widget.TextView;
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

public class AdminBuildDetailActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener {
    private ActivityAdminBuildDetailBinding binding;
    private Intent intent;
    private Bitmap bitmap;
    private Apartment apartment;
    private Dialog spinnerDialog,qrDialog;
    private Spinner spinner;
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
        spinner=binding.spinnerDate;
        spinner.setOnItemSelectedListener(this);
        binding.buttonDetailToEdit.setOnClickListener(this);
        binding.buttonMakeService.setOnClickListener(this);

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
                android.R.layout.simple_spinner_item, serviceArrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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
        final int buttonDialogCancel=R.id.buttonDetailDialogCancel;
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
            case buttonDialogCancel:
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
                         deleteImage(uriArrayList);
                         qrDialog.dismiss();
                     }
             }
                break;
        }
    }
    private void  deleteImage(ArrayList<Uri> uriArrayList){
        for (Uri uri :uriArrayList) {
            new File(String.valueOf(uri)).delete();
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
            QRGEncoder qrGenerator=new QRGEncoder(fileName,null,QRGContents.Type.TEXT,500);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position>0) {
            TextView textViewDialogDate,textViewDialogEmployee,textViewDialogCost;
            CheckBox checkBoxWell,checkBoxUp,checkBoxMachineRoom;
            Service spinnerService = (Service) parent.getSelectedItem();
            spinnerDialog = new Dialog(AdminBuildDetailActivity.this);
            spinnerDialog.setContentView(R.layout.alert_dialog_detail);
            spinnerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            textViewDialogDate = spinnerDialog.findViewById(R.id.textViewDetailDialogDate);
            textViewDialogEmployee = spinnerDialog.findViewById(R.id.textViewDetailDialogEmployee);
            textViewDialogCost= spinnerDialog.findViewById(R.id.textViewDetailDialogCost);
            checkBoxMachineRoom = spinnerDialog.findViewById(R.id.checkboxDetailDialogElevatorMachine);
            checkBoxUp = spinnerDialog.findViewById(R.id.checkboxDetailDialogElevatorTop);
            checkBoxWell = spinnerDialog.findViewById(R.id.checkboxDetailDialogWell);

            checkBoxWell.setChecked(spinnerService.getWell());
            checkBoxUp.setChecked(spinnerService.getElevatorUp());
            checkBoxMachineRoom.setChecked(spinnerService.getMachineRoom());

            textViewDialogEmployee.setText(spinnerService.getEmployee());
            textViewDialogDate.setText(spinnerService.getDate());
            textViewDialogCost.setText(spinnerService.getCost());
            spinnerDialog.findViewById(R.id.buttonDetailDialogCancel).setOnClickListener(this);
            spinnerDialog.show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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
                        ArrayList<Uri>uriArrayList=new ArrayList<>();
                        uriArrayList.add(uri);
                        sendMail(editTextMailForQRCOde.getText().toString(),uriArrayList);
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStart() {
        uriArrayList.clear();
        super.onStart();
    }

    @Override
    protected void onResume() {
        uriArrayList.clear();
        super.onResume();

    }
}