package com.grs21.supervisor.userFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.grs21.supervisor.R;
import com.grs21.supervisor.adapter.AdapterUserApartmentRecyclerView;
import com.grs21.supervisor.databinding.FragmentUserApartmentBinding;
import com.grs21.supervisor.model.Apartment;
import com.grs21.supervisor.model.User;
import com.grs21.supervisor.util.ToastMessage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserApartmentFragment extends Fragment {
    private FragmentUserApartmentBinding binding;
    private FirebaseFirestore fireStore =FirebaseFirestore.getInstance();
    private User currentUser;
    private Bundle bundle;
    public final String bundleKeyCurrentUser="currentUser";
    private CollectionReference collectionReference;
    private static final String TAG = "UserApartmentFragment";
    private ToastMessage toastMessage;
    private ArrayList<Apartment> apartments=new ArrayList<>();
    private AdapterUserApartmentRecyclerView adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
      binding=FragmentUserApartmentBinding.inflate(inflater,container,false);
      setHasOptionsMenu(true);
      toastMessage=new ToastMessage();
      bundle=getArguments();
      currentUser=(User) bundle.getSerializable(bundleKeyCurrentUser);
      getDataFromFireStore();
     return binding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.builds_search_menu, menu);
        MenuItem menuItem=menu.findItem(R.id.menuItemSearch);
        SearchView searchView=(SearchView) menuItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getDataFromFireStore() {
        collectionReference=fireStore.collection(currentUser.getCompany());
        collectionReference.orderBy("dateOfContract", Query.Direction.DESCENDING)
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                  if (error!=null){
                      Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                      Log.d(TAG, "onEvent: "+error.getMessage());
                  }
                 if (value!=null){
                     for (DocumentSnapshot snapshot:value.getDocuments()){
                          Map<String,Object> getData=snapshot.getData();
                          Apartment apartment=new Apartment( snapshot.getId(),(String)getData.get("buildName")
                         ,(String)getData.get("address"),(String)getData.get("cost"),(String)getData.get("managerName")
                         ,(String)getData.get("managerNumber"),(String)getData.get("managerAddress")
                         ,(String)getData.get("employeeName"),(String)getData.get("employeeNumber")
                         ,(String)getData.get("dateOfContract"),(String) getData.get("wellQRCOdeInfo")
                         ,(String) getData.get("elevatorUpQRCOdeInfo"),(String) getData.get("machineQRCOdeInfo")
                         ,(ArrayList<HashMap>) getData.get("service") ,(ArrayList<String>)getData.get("qrCodes"));
                         apartments.add(apartment);
                     }
                     LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                     adapter=new AdapterUserApartmentRecyclerView(apartments,currentUser);
                     binding.recyclerViewUserApartment.setAdapter(adapter);
                     binding.recyclerViewUserApartment.setLayoutManager(linearLayoutManager);
                     adapter.notifyDataSetChanged();
                 }
            }
        });
    }
}
