package com.grs21.supervisor;

import androidx.appcompat.app.AppCompatActivity;
import com.grs21.supervisor.databinding.ActivityUserBinding;
import android.os.Bundle;
import android.view.View;

public class UserActivity extends AppCompatActivity {

    private ActivityUserBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUserBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);
    }
}