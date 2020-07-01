package com.example.upit.student;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.upit.R;
import com.example.upit.databinding.FragmentStudentBinding;
import com.example.upit.main.MainActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StudentFragment extends Fragment {
    private FragmentStudentBinding binding;
    private StudentViewModel viewModel;
    private StudentAdapter adapter;

    public StudentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ViewBinding and DataBinding allows us to easily access views in the layout
        // with the syntax binding.viewId.
        // This removes the need for findViewById<Id> which can be messy.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_student, container, false);

        // Create the view model with the help of hilt (dependency injection)
        // The viewmodel constructor requires 2 parameters (Firestore, FirebaseAuth)
        // Hilt will automatically inject that for you since it is declared in the
        // module under com.example.upit.di.HiltModule
        viewModel = new ViewModelProvider(this).get(StudentViewModel.class);

        // Create the adapter for the recycler view
        adapter = new StudentAdapter();

        // Attach the viewModel created to the viewModel on the xml layout
        binding.setViewModel(viewModel);

        // Set this fragment as the lifecycle owner
        binding.setLifecycleOwner(this);

        // Attach the adapter to the recycler view
        binding.recyclerStudents.setAdapter(adapter);

        // Add a layout manager to the recycler view
        // LinearLayoutManager is for recycler view that will only have rows or columns
        // Alternatively, GridLayoutManager is needed if you want the recycler view to
        // have grids instead
        binding.recyclerStudents.setLayoutManager(new LinearLayoutManager(getContext()));

        // Change the toolbar title to "Students"
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Students");

        // Create the observer for the students live data
        // This will be called whenever any changes to the live data occurs
        viewModel.getStudents().observe(getViewLifecycleOwner(), students -> {
            // Hide the recycler view and show the "empty" views if there are no students
            if(students.size() == 0) {
                binding.recyclerStudents.setVisibility(View.GONE);
                binding.textEmpty.setVisibility(View.VISIBLE);
                binding.imageEmpty.setVisibility(View.VISIBLE);
            }
            // Do the opposite if there are students found
            else {
                binding.recyclerStudents.setVisibility(View.VISIBLE);
                binding.textEmpty.setVisibility(View.GONE);
                binding.imageEmpty.setVisibility(View.GONE);
            }

            // Submit the new list to the recycler view adapter
            adapter.submitList(students);
        });

        // Create the observer for the addStudents flag live data
        // This will be called whenever any changes to the live data occurs
        viewModel.getAddStudent().observe(getViewLifecycleOwner(), addStudent -> {
            // If addStudent == true, then perform the code block inside
            if(addStudent) {
                // Navigate to the newStudentFragment
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_studentFragment_to_newStudentFragment);

                // Reset the flag by setting addStudent to false
                viewModel.studentAdded();
            }
        });

        // setHasOptionMenu(true) allows onOptionsItemSelected to listen to clicks on the action bar
        setHasOptionsMenu(true);

        return binding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_student) {
            // Tells the viewmodel that the user wants to add a new student
            // this sets the value of the addStudent live data to true
            viewModel.addNewStudent();
        }
        return super.onOptionsItemSelected(item);
    }
}