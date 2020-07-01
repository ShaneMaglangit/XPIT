package com.example.upit.courselist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.upit.R;
import com.example.upit.main.MainActivity;
import com.example.upit.databinding.FragmentCourseListBinding;
import com.example.upit.util.ClickListener;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CourseListFragment extends Fragment {
    private CourseListViewModel viewModel;
    private FragmentCourseListBinding binding;
    private CourseAdapter adapter;

    public CourseListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ViewBinding and DataBinding allows us to easily access views in the layout
        // with the syntax binding.viewId.
        // This removes the need for findViewById<Id> which can be messy.
        binding = FragmentCourseListBinding.inflate(inflater, container, false);

        // Create the view model with the help of hilt (dependency injection)
        // The viewmodel constructor requires 2 parameters (Firestore, FirebaseAuth)
        // Hilt will automatically inject that for you since it is declared in the
        // module under com.example.upit.di.HiltModule
        viewModel = new ViewModelProvider(this).get(CourseListViewModel.class);

        // Change the toolbar title to "Courses"
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Courses");

        // Load the user info
        viewModel.loadUserInfo(((MainActivity) getActivity()).getUser());

        // Set up the course adapter
        adapter = new CourseAdapter(new ClickListener() {
            @Override
            public <T> void onClick(T item) {
                String courseId = (String) item;

                // Open the course detail if an item is clicked
                NavHostFragment.findNavController(CourseListFragment.this)
                    .navigate(CourseListFragmentDirections
                            .actionCourseListFragmentToCourseDetailFragment(courseId)
                    );
            }
        });

        // Attach the adapter to the recycler view
        binding.recyclerCourses.setAdapter(adapter);

        // Add a layout manager to the recycler view
        // LinearLayoutManager is for recycler view that will only have rows or columns
        // Alternatively, GridLayoutManager is needed if you want the recycler view to
        // have grids instead
        binding.recyclerCourses.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the observers
        viewModel.getCourses().observe(getViewLifecycleOwner(), courses -> {
            Log.i("CourseListFragment", String.valueOf(courses.size()));
            if(courses.size() == 0) {
                binding.recyclerCourses.setVisibility(View.GONE);
                binding.textEmpty.setVisibility(View.VISIBLE);
                binding.imageEmpty.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerCourses.setVisibility(View.VISIBLE);
                binding.textEmpty.setVisibility(View.GONE);
                binding.imageEmpty.setVisibility(View.GONE);
            }

            adapter.submitList(courses);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if(!error.isEmpty()) {
                new MaterialDialog.Builder(getActivity())
                    .setTitle("Problem encountered")
                    .setMessage(error)
                    .setPositiveButton("Ok", (dialogInterface, which1) -> {
                        dialogInterface.dismiss();
                    })
                    .setAnimation(R.raw.error)
                    .build()
                    .show();
            }
        });

        // Attach the viewModel created to the viewModel on the xml layout
        binding.setViewModel(viewModel);

        // Set this fragment as the lifecycle owner
        binding.setLifecycleOwner(this);

        // Set menu items
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_course) {
            final EditText courseEditText = new EditText(getContext());
            courseEditText.setHint("Enter course name");
            courseEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            courseEditText.setMaxLines(1);
            courseEditText.setPadding(32, 32, 32, 32);

            new AlertDialog.Builder(getContext())
                .setTitle("Add course")
                .setView(courseEditText)
                .setCancelable(false)
                .setPositiveButton("Add", (dialog, which) -> {
                    String courseName = courseEditText.getText().toString();

                    if(courseName.equals("")) {
                        viewModel.showError("Please enter a valid course name");
                    } else {
                        // Add new course to firestore
                        viewModel.saveCourse(((MainActivity) getActivity()).getUser(), courseName);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
        }
        return super.onOptionsItemSelected(item);
    }
}