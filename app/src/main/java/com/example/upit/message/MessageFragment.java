package com.example.upit.message;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.upit.R;
import com.example.upit.coursedetail.CourseDetailFragmentArgs;
import com.example.upit.data.Conversation;
import com.example.upit.databinding.FragmentMessageBinding;
import com.example.upit.main.MainActivity;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.LinkedList;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MessageFragment extends Fragment {
    private MessageAdapter adapter;
    private FragmentMessageBinding binding;
    private MessageViewModel viewModel;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayoutManager layoutManager;
        MainActivity mainActivity = (MainActivity) getActivity();
        Conversation currentCon = MessageFragmentArgs.fromBundle(getArguments()).getConversation();

        // ViewBinding and DataBinding allows us to easily access views in the layout
        // with the syntax binding.viewId.
        // This removes the need for findViewById<Id> which can be messy.
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_message, container, false);

        // Create the view model with the help of hilt (dependency injection)
        // The viewmodel constructor requires 2 parameters (Firestore, FirebaseAuth)
        // Hilt will automatically inject that for you since it is declared in the
        // module under com.example.upit.di.HiltModule
        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        viewModel.loadConversation(currentCon, mainActivity.getUser());

        // Attach the viewModel created to the viewModel on the xml layout
        binding.setViewModel(viewModel);

        // Set this fragment as the lifecycle owner
        binding.setLifecycleOwner(this);

        // Set up the recycler view
        adapter = new MessageAdapter(mainActivity.getUser());
        layoutManager = new LinearLayoutManager(getContext());

        // Set stack from end to start the items from the bottom
        layoutManager.setStackFromEnd(true);

        // Attach the adapter to the recycler view
        binding.recyclerMessages.setAdapter(adapter);

        // Add a layout manager to the recycler view
        // LinearLayoutManager is for recycler view that will only have rows or columns
        // Alternatively, GridLayoutManager is needed if you want the recycler view to
        // have grids instead
        binding.recyclerMessages.setLayoutManager(layoutManager);

        // Set up the action bar title based on the name of the other member in the conversation
        viewModel.getConversation().observe(getViewLifecycleOwner(), conversation -> {
            if(conversation != null) {
                if (conversation.getMembers().indexOf(mainActivity.getUser().getName()) == 0) {
                    mainActivity.getSupportActionBar().setTitle(conversation.getMembers().get(1));
                } else {
                    mainActivity.getSupportActionBar().setTitle(conversation.getMembers().get(0));
                }
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

        // Attach an observer to the recentMessages live data.
        // Updates the conversation if any message is added.
        viewModel.getRecentMessages().observe(getViewLifecycleOwner(), messages -> {
            if(messages != null) {
                adapter.submitList(messages);
            }
        });

        return binding.getRoot();
    }
}