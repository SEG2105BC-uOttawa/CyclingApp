package com.example.cyclingapp.ui.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cyclingapp.data.model.Role;
import com.example.cyclingapp.databinding.FragmentEventBinding;

public class EventFragment extends Fragment {

    private FragmentEventBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEventBinding.inflate(inflater, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String displayName = bundle.getString("displayName", "User");
            Role role = (Role) bundle.getSerializable("role");

            String welcomeMessage = "Welcome, " + displayName + "!" + "\n" + "You are logged in as \"" + role + "\".";
            Toast.makeText(getActivity(), welcomeMessage, Toast.LENGTH_LONG).show();

            // Enable button only if the user is an admin
            if (role != null && role.equals(Role.ADMIN)) {
                binding.btnAdd.setEnabled(true);
                binding.btnAdd.setOnClickListener(v -> navigateToEventCreate());
            }
        }

        binding.btnList.setOnClickListener(v -> navigateToEventList());

        return binding.getRoot();
    }

    private void navigateToEventCreate() {
        Intent intent = new Intent(getActivity(), EventCreate.class);
        startActivity(intent);
    }

    private void navigateToEventList() {
        Intent intent = new Intent(getActivity(), EventList.class);
        startActivity(intent);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
