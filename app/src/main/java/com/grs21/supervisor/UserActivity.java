package com.grs21.supervisor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.grs21.supervisor.adminFragment.AddFragment;
import com.grs21.supervisor.adminFragment.ApartmentFragment;
import com.grs21.supervisor.adminFragment.RepairFragment;
import com.grs21.supervisor.databinding.ActivityUserBinding;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

public class UserActivity extends AppCompatActivity {
    private static final String TAG = "UserActivity";
    private ActivityUserBinding binding;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
        initializeToolBar();
        initializeBottomNavigationBar();
        Log.d(TAG, "onCreate:+++++++++++ loginUser");

    }

    private void initializeToolBar() {
        toolbar=binding.toolBarUser;
        setSupportActionBar(toolbar);
    }
    private void initializeBottomNavigationBar() {
        BottomNavigationView bottomNavigationView=findViewById(R.id.userBottomNavigationBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(userNavigationListener);
        /* ilk açıldığında hangi açılıcak fragment
        ApartmentFragment apartmentFragment=new ApartmentFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.userFragmentContainer,apartmentFragment)
                .commit();*/
    }

    private BottomNavigationView.OnNavigationItemSelectedListener userNavigationListener
            =new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            final int menuApartmentList=R.id.menuItemUserApartmentList;
            final int menuRepair=R.id.menuItemUserRepair;
            Fragment selectedFragment=null;
            switch (item.getItemId()){

                case menuApartmentList:
                   // selectedFragment= new ApartmentFragment();

                    break;
                case menuRepair:
                 //   selectedFragment=new RepairFragment();

                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.admin_fragmentContainer
                    , selectedFragment).commit();
            return true;
        }
    };


}