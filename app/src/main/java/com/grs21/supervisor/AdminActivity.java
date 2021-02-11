package com.grs21.supervisor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grs21.supervisor.databinding.ActivityAdminBinding;
import com.grs21.supervisor.adminFragment.AddFragment;
import com.grs21.supervisor.adminFragment.ApartmentFragment;
import com.grs21.supervisor.adminFragment.RepairFragment;
import com.grs21.supervisor.adminFragment.ServiceFragment;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {
    private ArrayList<String> add=new ArrayList<>();
    private ActivityAdminBinding binding;
    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAdminBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        DrawerLayout drawerLayout=findViewById(R.id.drawerLayout);
        Toolbar toolbar=findViewById(R.id.toolBar);

        setSupportActionBar(toolbar);

        BottomNavigationView navigationView=findViewById(R.id.admin_bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(adminNavigationListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragmentContainer,new ApartmentFragment())
                .commit();

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
                    selectedFragment=new ServiceFragment();
                    break;
                case R.id.menuItemRepair:
                    selectedFragment=new RepairFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragmentContainer, selectedFragment).commit();
            return true;
        }
    };
}