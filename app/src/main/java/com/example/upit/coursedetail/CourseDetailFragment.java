package com.example.upit.coursedetail;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Messenger;
import android.provider.OpenableColumns;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.upit.R;
import com.example.upit.databinding.FragmentCourseDetailBinding;
import com.example.upit.main.MainActivity;
import com.example.upit.util.ClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CourseDetailFragment extends Fragment {
    private FragmentCourseDetailBinding binding;
    private CourseDetailViewModel viewModel;
    private CourseStudentAdapter courseStudentAdapter;
    private CourseFileAdapter courseFileAdapter;
    private MaterialDialog uploadingDialog;

    public CourseDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ViewBinding and DataBinding allows us to easily access views in the layout
        // with the syntax binding.viewId.
        // This removes the need for findViewById<Id> which can be messy.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_course_detail, container, false);

        // Create the view model with the help of hilt (dependency injection)
        // The viewmodel constructor requires 2 parameters (Firestore, FirebaseAuth)
        // Hilt will automatically inject that for you since it is declared in the
        // module under com.example.upit.di.HiltModule
        viewModel = new ViewModelProvider(this).get(CourseDetailViewModel.class);
        viewModel.loadCourse(CourseDetailFragmentArgs.fromBundle(getArguments()).getCourseId());

        // Change the toolbar title to "Detail"
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Detail");

        // Hide upload and add button for students
        if(((MainActivity) getActivity()).getUser().getAccountType().equals("STUDENT")) {
            binding.buttonUpload.setVisibility(View.GONE);
            binding.buttonAdd.setVisibility(View.GONE);
        }

        // Create the adapter
        courseStudentAdapter = new CourseStudentAdapter();
        binding.recyclerStudents.setAdapter(courseStudentAdapter);
        binding.recyclerStudents.setLayoutManager(new LinearLayoutManager(getContext()));

        courseFileAdapter = new CourseFileAdapter(new ClickListener() {
            @Override
            public <T> void onClick(T item) {
                String path = (String) item;

                // Request permission for writing to external storage
                if (ContextCompat.checkSelfPermission(
                        getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.downloadFile(path);
                    Snackbar.make(binding.getRoot(), "Downloading file " + path, Snackbar.LENGTH_SHORT).show();
                } else {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
            }
        });

        // Attach the adapter to the recycler view
        binding.recyclerFiles.setAdapter(courseFileAdapter);

        // Add a layout manager to the recycler view
        // LinearLayoutManager is for recycler view that will only have rows or columns
        // Alternatively, GridLayoutManager is needed if you want the recycler view to
        // have grids instead
        binding.recyclerFiles.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set up the observers
        viewModel.getCourse().observe(getViewLifecycleOwner(), course -> {
            if(course != null) {
                viewModel.updateLists();
            }
        });

        viewModel.getStudents().observe(getViewLifecycleOwner(), students -> {
            // Toggle view visibility based on the student list
            if(students.size() > 0) {
                binding.recyclerStudents.setVisibility(View.VISIBLE);
                binding.textEmptyStudents.setVisibility(View.GONE);
            } else {
                binding.recyclerStudents.setVisibility(View.GONE);
                binding.textEmptyStudents.setVisibility(View.VISIBLE);
            }

            // Submit the list to the adapter
            courseStudentAdapter.submitList(students);
        });

        viewModel.getDocuments().observe(getViewLifecycleOwner(), documents -> {
            // Toggle view visibility based on the documents list
            if(documents.size() > 0) {
                binding.recyclerFiles.setVisibility(View.VISIBLE);
                binding.textEmptyFiles.setVisibility(View.GONE);
            } else {
                binding.recyclerFiles.setVisibility(View.GONE);
                binding.textEmptyFiles.setVisibility(View.VISIBLE);
            }

            // Submit the list of documents to the adapter
            courseFileAdapter.submitList(documents);
        });

        viewModel.getEmailInput().observe(getViewLifecycleOwner(), emailInput -> {
            if(emailInput) {
                // Edit text for email
                final EditText emailEditText = new EditText(getContext());
                emailEditText.setHint("Enter student email");
                emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                emailEditText.setPadding(32, 32, 32, 32);

                // Create the dialog
                new AlertDialog.Builder(getContext())
                        .setTitle("Add student")
                        .setView(emailEditText)
                        .setCancelable(false)
                        .setPositiveButton("Add", (dialog, which) -> {
                            String email = emailEditText.getText().toString();

                            if(email.equals("")) {
                                viewModel.showError("Please enter a valid email address");
                            } else {
                                // Add new course to firestore
                                viewModel.addStudent(email);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

                viewModel.emailInputShown();
            }
        });

        // Show an error message dialog
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

        // Show a loading dialog
        viewModel.getUploading().observe(getViewLifecycleOwner(), uploading -> {
            // Create the dialog if it isn't created yet
            if(uploadingDialog == null) {
                uploadingDialog = new MaterialDialog.Builder(getActivity())
                        .setTitle("Uploading file")
                        .setMessage("Please wait while the file is being uploaded")
                        .setCancelable(false)
                        .setAnimation(R.raw.download)
                        .build();
            }

            // Hide or show the dialog
            if(uploading) {
                uploadingDialog.show();
            } else {
                uploadingDialog.dismiss();
            }
        });

        // Get the file to upload from the file manager
        viewModel.getFileInput().observe(getViewLifecycleOwner(), fileInput -> {
            if(fileInput) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("*/*");
                startActivityForResult(intent, 0);
                viewModel.fileInputShown();
            }
        });

        // hide upload and add students if student
        if(((MainActivity) getActivity()).getUser().getAccountType() == "STUDENT") {
            binding.buttonAdd.setVisibility(View.GONE);
            binding.buttonUpload.setVisibility(View.GONE);
        }

        // Attach the viewModel created to the viewModel on the xml layout
        binding.setViewModel(viewModel);

        // Set this fragment as the lifecycle owner
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && data != null) {
            ContentResolver contentResolver = getActivity().getContentResolver();

            // Get the file URI and pass it to the view model to upload it
            try {
                Cursor dataCursor = contentResolver.query(data.getData(), null, null, null, null);
                int nameIndex = dataCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                dataCursor.moveToFirst();

                String fileName = dataCursor.getString(nameIndex);
                viewModel.uploadFile(fileName, data.getData());
            } catch (SecurityException e) {
                viewModel.showError("Unsupported File Type");
            }
        }
    }
}