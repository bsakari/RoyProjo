package com.roysam.docgari.ui.customer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roysam.docgari.R;
import com.roysam.docgari.models.MechanicsAdapter;
import com.roysam.docgari.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CustomerFragment extends Fragment {
    RecyclerView mRecyclerMechanics;
    MechanicsAdapter adapter;
    ArrayList<User> mechanics;
    String privilege;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_customer, container, false);
        Intent intent1 = getActivity().getIntent();
        Bundle bundle = intent1.getExtras();
        privilege = "1";
        if(bundle != null){
            privilege = bundle.getString("privilege");
        }
        if (privilege.equals("2")){
            NavHostFragment.findNavController(getParentFragment())
                    .navigate(R.id.nav_gallery);
        }
        mRecyclerMechanics = root.findViewById(R.id.mRecyclerMechanics);
        mechanics = new ArrayList<>();
        mRecyclerMechanics.setHasFixedSize(true);
        mRecyclerMechanics.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MechanicsAdapter(getContext(),mechanics);
        mRecyclerMechanics.setAdapter(adapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading");
        dialog.setMessage("Please wait...");
        dialog.show();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                dialog.dismiss();
                for (DataSnapshot snap : snapshot.getChildren()){
                    User mechanic = snap.getValue(User.class);
                    User my_user = new User(mechanic.getName(),mechanic.getEmail(),mechanic.getPhone(),mechanic.getStatus(),mechanic.getLon(),mechanic.getLat(),mechanic.getKey());
                    mechanics.add(my_user);
                    Log.d("it_worked", "onDataChange: "+mechanic.getEmail());
                    Log.d("it_worked", "onDataChange: "+mechanic.getName());
                    Log.d("it_worked", "onDataChange: "+mechanic.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        return root;
    }

}