package com.grs21.supervisor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grs21.supervisor.databinding.ActivityAdminBinding;
import com.grs21.supervisor.adminFragment.AddFragment;
import com.grs21.supervisor.adminFragment.ApartmentFragment;
import com.grs21.supervisor.adminFragment.RepairFragment;
import com.grs21.supervisor.model.User;
import com.onesignal.OneSignal;


public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityAdminBinding binding;
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fireStore;
    private User currentUser;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private  Bundle bundleCurrentUserData=new Bundle();
    private static final String TAG = "AdminActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAdminBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        firebaseAuth=FirebaseAuth.getInstance();
        fireStore=FirebaseFirestore.getInstance();

        Intent intent=getIntent();
        currentUser=(User)intent.getSerializableExtra("currentUser");

        drawerLayout=binding.drawerLayout;
        toolbar=binding.toolBarAdmin;

        setSupportActionBar(toolbar);
        bundleCurrentUserData.putSerializable("currentUser", currentUser);

        initializeBottomNavigationBar();
        initializeNavigationMenu();
        userPhoneIdControl(OneSignal.getDeviceState().getUserId());

    }

    private void userPhoneIdControl(String userPhoneId) {
        if (!userPhoneId.equals(currentUser.getPhoneID())){
            Log.d(TAG, "userPhoneIdControl: "+firebaseAuth.getCurrentUser().getUid());
            fireStore.collection("Users").document(firebaseAuth.getCurrentUser().getUid())
                    .update("phoneID",userPhoneId).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AdminActivity.this, "PhoneID değiştirildi", Toast.LENGTH_LONG).show();
                }
            });
        }else
        {
            Toast.makeText(this, "PhoneID değiştilmedi", Toast.LENGTH_SHORT).show();
        }
    }


    private void initializeBottomNavigationBar() {
        BottomNavigationView bottomNavigationView=findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(adminNavigationListener);


        ApartmentFragment apartmentFragment=new ApartmentFragment();
        apartmentFragment.setArguments(bundleCurrentUserData);
        getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragmentContainer,apartmentFragment)
                .commit();
    }
    private void initializeNavigationMenu() {
        NavigationView navigationView=binding.navigationView;
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);
        TextView textViewUsername= headerLayout.findViewById(R.id.textViewNavigationUserName);
        textViewUsername.setText(currentUser.getUserName());
        TextView textViewAccessLevel= headerLayout.findViewById(R.id.textViewNavigationAccessLwl);
        textViewAccessLevel.setText(currentUser.getAccessLevel());
        navigationView.setNavigationItemSelectedListener(this);

        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar
                ,R.string.navigation_drawer_open,R.string.navigation_drawer_close );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener adminNavigationListener
            =new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            final int menuBuildAdd=R.id.menuItemAdd;
            final int menuApartmentList=R.id.menuItemApartmentList;
            final int menuRepair=R.id.menuItemRepair;
            Fragment selectedFragment=null;
            switch (item.getItemId()){
                case menuBuildAdd:
                    selectedFragment=new AddFragment();
                    selectedFragment.setArguments(bundleCurrentUserData);
                    break;
                case menuApartmentList:
                    selectedFragment= new ApartmentFragment();
                    selectedFragment.setArguments(bundleCurrentUserData);
                    break;
                case menuRepair:
                    selectedFragment=new RepairFragment();
                    selectedFragment.setArguments(bundleCurrentUserData);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragmentContainer
                    , selectedFragment).commit();
            return true;
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int logOut=R.id.logOut;
        final int registration=R.id.registration;
        switch (item.getItemId()){
            case logOut:
                if (firebaseAuth!=null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
                break;
            case registration:
                Intent intent=new Intent(getApplicationContext(), RegistrationActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
                break;
        }
        return true;
    }


}