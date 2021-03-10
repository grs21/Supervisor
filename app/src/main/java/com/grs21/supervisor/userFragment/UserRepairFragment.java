package com.grs21.supervisor.userFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grs21.supervisor.databinding.FragmentUserRepairBinding;
import com.grs21.supervisor.model.User;

public class UserRepairFragment extends Fragment {
    private FragmentUserRepairBinding binding;
    private User currentUser;
    private Bundle bundle;
    public final String bundleKeyCurrentUser="currentUser";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding=FragmentUserRepairBinding.inflate(inflater,container,false);
        bundle=getArguments();
        currentUser=(User) bundle.getSerializable(bundleKeyCurrentUser);


        return binding.getRoot();
    }
}
