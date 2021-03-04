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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.grs21.supervisor.databinding.ActivityAdminBinding;
import com.grs21.supervisor.adminFragment.AddFragment;
import com.grs21.supervisor.adminFragment.ApartmentFragment;
import com.grs21.supervisor.adminFragment.RepairFragment;
import com.grs21.supervisor.model.User;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ArrayList<String> add=new ArrayList<>();
    private ActivityAdminBinding binding;
    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore fireStore;
    private Fragment repairFragment=new RepairFragment();
    private User currentUser;
    private static final String TAG = "AdminActivity";
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
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
        initializeBottomNavigationBar();
        initializeNavigationMenu();

    }

    private void initializeBottomNavigationBar() {
        BottomNavigationView bottomNavigationView=findViewById(R.id.admin_bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(adminNavigationListener);

        Bundle bundle=new Bundle();
        bundle.putSerializable("currentUser", currentUser);
        ApartmentFragment apartmentFragment=new ApartmentFragment();
        apartmentFragment.setArguments(bundle);
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
            Fragment selectedFragment=null;
            switch (item.getItemId()){
                case R.id.menuItemAdd:
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("currentUser", currentUser);
                    selectedFragment=new AddFragment();
                    selectedFragment.setArguments(bundle);
                    break;
                case R.id.menuItemApartmentList:
                    Bundle bundle1=new Bundle();
                    bundle1.putSerializable("currentUser", currentUser);
                    selectedFragment= new ApartmentFragment();
                    selectedFragment.setArguments(bundle1);
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
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
                break;
            case R.id.registration:
                Intent intent=new Intent(getApplicationContext(), RegistrationActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
                break;
        }
        return true;
    }


}