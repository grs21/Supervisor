package com.grs21.supervisor.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.grs21.supervisor.LoginActivity;
import com.grs21.supervisor.R;
import com.grs21.supervisor.databinding.ActivityUserBinding;
import com.grs21.supervisor.model.User;
import com.grs21.supervisor.userFragment.UserApartmentFragment;
import com.grs21.supervisor.userFragment.UserRepairFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity implements View.OnClickListener,NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "UserActivity";
    private ActivityUserBinding binding;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private final Bundle bundle=new Bundle();
    private User currentUser;
    private DrawerLayout drawerLayout;
    public final String keyCurrentUser="currentUser";
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);

        Intent intent=getIntent();
        currentUser=(User) intent.getSerializableExtra(keyCurrentUser);
        initializeToolBar();
        initializeBottomNavigationBar();
        initializeNavigationMenu();
    }

    private void initializeNavigationMenu() {
        NavigationView navigationView=binding.navigationViewUser;
        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header);
        TextView textViewUsername= headerLayout.findViewById(R.id.textViewNavigationUserName);
        textViewUsername.setText(currentUser.getFullName());
        TextView textViewAccessLevel= headerLayout.findViewById(R.id.textViewNavigationAccessLwl);
        textViewAccessLevel.setText(currentUser.getAccessLevel());
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout=binding.userDrawerLayout;
        toggle=new ActionBarDrawerToggle(this,drawerLayout,toolbar
                ,R.string.navigation_drawer_open,R.string.navigation_drawer_close );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        final int logOut=R.id.logOut;
        if (item.getItemId() == logOut) {
            if (firebaseAuth != null) {

                finish();
                firebaseAuth.signOut();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        }
        return true;
    }
    private void initializeToolBar() {
        toolbar=binding.toolBarUser;
        setSupportActionBar(toolbar);
    }
    private void initializeBottomNavigationBar() {
        BottomNavigationView bottomNavigationView=findViewById(R.id.userBottomNavigationBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(userNavigationListener);
        bundle.putSerializable("currentUser",currentUser);
        UserApartmentFragment apartmentFragment=new UserApartmentFragment();
        apartmentFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.userFragmentContainer,apartmentFragment)
                .commit();
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
                    selectedFragment= new UserApartmentFragment();
                    selectedFragment.setArguments(bundle);
                    break;
                case menuRepair:
                    selectedFragment=new UserRepairFragment();
                    selectedFragment.setArguments(bundle);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.userFragmentContainer
                    , selectedFragment).commit();
            return true;
        }
    };
    @Override
    public void onClick(View v) {
    }
}