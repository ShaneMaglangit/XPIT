package com.example.upit.contact;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.upit.R;
import com.example.upit.data.Conversation;
import com.example.upit.databinding.FragmentContactBinding;
import com.example.upit.main.MainActivity;
import com.example.upit.util.ClickListener;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContactFragment extends Fragment {
    private FragmentContactBinding binding;
    private ContactViewModel viewModel;
    private ContactAdapter adapter;

    public ContactFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ViewBinding and DataBinding allows us to easily access views in the layout
        // with the syntax binding.viewId.
        // This removes the need for findViewById<Id> which can be messy.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_contact, container, false);

        // Create the view model with the help of hilt (dependency injection)
        // The viewmodel constructor requires 2 parameters (Firestore, FirebaseAuth)
        // Hilt will automatically inject that for you since it is declared in the
        // module under com.example.upit.di.HiltModule
        viewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        viewModel.loadContacts(((MainActivity) getActivity()).getUser());

        // Change the toolbar title to "Contacts"
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Contacts");

        // Attach the viewModel created to the viewModel on the xml layout
        binding.setViewModel(viewModel);

        // Set this fragment as the lifecycle owner
        binding.setLifecycleOwner(this);

        // Create the adapter for the recycler view
        adapter = new ContactAdapter(new ClickListener() {
            @Override
            public <T> void onClick(T item) {
                Conversation conversation = (Conversation) item;
                viewModel.getOpenConversation().setValue(conversation);
            }
        }, ((MainActivity) getActivity()).getUser());

        binding.recyclerContacts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerContacts.setAdapter(adapter);

        viewModel.getContacts().observe(getViewLifecycleOwner(), contacts -> {
            // Toggle the view visibility based on the contact size
            if(contacts.size() == 0) {
                binding.textEmpty.setVisibility(View.VISIBLE);
                binding.imageEmpty.setVisibility(View.VISIBLE);
                binding.recyclerContacts.setVisibility(View.GONE);
            } else {
                binding.textEmpty.setVisibility(View.GONE);
                binding.imageEmpty.setVisibility(View.GONE);
                binding.recyclerContacts.setVisibility(View.VISIBLE);
            }

            adapter.submitList(contacts);
        });

        // Open the conversation
        viewModel.getOpenConversation().observe(getViewLifecycleOwner(), conversation -> {
            if(conversation != null) {
                NavHostFragment.findNavController(this)
                    .navigate(ContactFragmentDirections.actionContactFragmentToMessageFragment(conversation));
                viewModel.conversationShown();
            }
        });

        // Used when trying to create a new conversation
        viewModel.getEmailInput().observe(getViewLifecycleOwner(), emailInput -> {
            if(emailInput) {
                final EditText emailEditText = new EditText(getContext());
                emailEditText.setHint("Enter student email");
                emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                emailEditText.setPadding(32, 32, 32, 32);

                new AlertDialog.Builder(getContext())
                    .setTitle("Enter user email")
                    .setView(emailEditText)
                    .setCancelable(false)
                    .setPositiveButton("Send message", (dialog, which) -> {
                        String email = emailEditText.getText().toString();

                        if(email.equals("")) {
                            viewModel.showError("Please enter a valid email");
                        } else {
                            viewModel.createConversation(email);
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

                viewModel.emailInputShown();
            }
        });

        // Shows an error message
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

        return binding.getRoot();
    }
}