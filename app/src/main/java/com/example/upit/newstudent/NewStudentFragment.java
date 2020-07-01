package com.example.upit.newstudent;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.upit.R;
import com.example.upit.databinding.FragmentNewStudentBinding;
import com.example.upit.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NewStudentFragment extends Fragment {
    private FragmentNewStudentBinding binding;
    private NewStudentViewModel viewModel;

    public NewStudentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ViewBinding and DataBinding allows us to easily access views in the layout
        // with the syntax binding.viewId.
        // This removes the need for findViewById<Id> which can be messy.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_student, container, false);

        // Change the toolbar title to "Add new student"
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Add new student");

        // Create the view model with the help of hilt (dependency injection)
        // The viewmodel constructor requires 2 parameters (Firestore, FirebaseAuth)
        // Hilt will automatically inject that for you since it is declared in the
        // module under com.example.upit.di.HiltModule
        viewModel = new ViewModelProvider(this).get(NewStudentViewModel.class);

        // Attach an observer for the navigateBack flag live data
        viewModel.getNavigateBack().observe(getViewLifecycleOwner(), navigateBack -> {
            // If navigateBack == true, then perform the code block inside
            if(navigateBack) {
                // Navigate back to the previous fragment
                NavHostFragment.findNavController(this).navigateUp();

                // Reset the flag, set the navigateBack to false
                viewModel.navigateBackDone();
            }
        });

        // Attach an observer for the error live data
        // This live data will contain a string that represents the error message
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            // If the live data is empty, then there is no error message
            // Therefore, only perform this block is the error live data contains something
            if(!error.isEmpty()) {
                // Show an error dialog
                new MaterialDialog.Builder(getActivity())
                        .setTitle("An error has occurred")
                        .setMessage(error)
                        .setPositiveButton("Ok", (dialogInterface, which) -> dialogInterface.dismiss())
                        .setAnimation(R.raw.error)
                        .build()
                        .show();

                // Clear the live data for errors
                viewModel.closeErrorMessage();
            }
        });

        // Attach the viewModel created to the viewModel on the xml layout
        binding.setViewModel(viewModel);

        // Set this fragment as the lifecycle owner
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }
}