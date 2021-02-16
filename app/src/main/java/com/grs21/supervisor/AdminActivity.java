package com.grs21.supervisor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.grs21.supervisor.databinding.ActivityAdminBinding;
import com.grs21.supervisor.adminFragment.AddFragment;
import com.grs21.supervisor.adminFragment.ApartmentFragment;
import com.grs21.supervisor.adminFragment.RepairFragment;
import com.grs21.supervisor.adminFragment.ServiceFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ArrayList<String> add=new ArrayList<>();
    private ActivityAdminBinding binding;
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;
    TextView textViewAccessLwl;
    TextView textViewUserName;
    private Fragment repairFragment=new RepairFragment();
    private Fragment apartmentFragment=new ApartmentFragment();
    private Fragment serviceFragment=new ServiceFragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAdminBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        drawerLayout=binding.drawerLayout;
        Toolbar toolbar=binding.toolBarAdmin;
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView=findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(adminNavigationListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragmentContainer,new ApartmentFragment())
                .commit();

        NavigationView navigationView=binding.navigationView;

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar
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
            Fragment selectedFragment=null;
            switch (item.getItemId()){
                case R.id.menuItemAdd:
                    selectedFragment=new AddFragment();
                    break;
                case R.id.menuItemApartmentList:
                    selectedFragment=new ApartmentFragment();
                    break;
                case R.id.menuItemService:
                    selectedFragment=serviceFragment;
                    break;
                case R.id.menuItemRepair:
                    selectedFragment=repairFragment;
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragmentContainer, selectedFragment).commit();
            return true;
        }
    };



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                if (firebaseAuth!=null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                }
                break;
            case R.id.registration:
                startActivity(new Intent(getApplicationContext(),RegistrationActivity.class));
                break;
        }
        return true;
    }
}