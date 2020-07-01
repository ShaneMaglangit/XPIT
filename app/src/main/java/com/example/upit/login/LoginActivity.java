package com.example.upit.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.upit.main.MainActivity;
import com.example.upit.R;
import com.example.upit.databinding.ActivityLoginBinding;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ViewBinding and DataBinding allows us to easily access views in the layout
        // with the syntax binding.viewId.
        // This removes the need for findViewById<Id> which can be messy.
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        // Create the view model with the help of hilt (dependency injection)
        // The viewmodel constructor requires 2 parameters (Firestore, FirebaseAuth)
        // Hilt will automatically inject that for you since it is declared in the
        // module under com.example.upit.di.HiltModule
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Set up the observers
        viewModel.getError().observe(this, s -> {
            if(!s.isEmpty()) {
                new MaterialDialog.Builder(this)
                        .setTitle("An error has occurred")
                        .setMessage(s)
                        .setPositiveButton("Ok", (dialogInterface, which) -> dialogInterface.dismiss())
                        .setAnimation(R.raw.error)
                        .build()
                        .show();
                viewModel.closeErrorMessage();
            }
        });

        // Navigates to the main activity is login is successfull
        viewModel.getUser().observe(this, user -> {
            if(user != null) {
                // Create the intent
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("current_user", user);

                // Destroy this activity
                finishAffinity();

                // Start the activity
                startActivity(intent);

                // Reset flag
                viewModel.loggedIn();
            }
        });

        // Attach the viewModel created to the viewModel on the xml layout
        binding.setViewModel(viewModel);

        // Set this fragment as the lifecycle owner
        binding.setLifecycleOwner(this);

        // Set the content view
        setContentView(binding.getRoot());
    }

    // Hide the keyboard when area outside of edit text is clicked
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if (view != null && view instanceof EditText) {
                Rect r = new Rect();
                view.getGlobalVisibleRect(r);
                int rawX = (int) ev.getRawX();
                int rawY = (int) ev.getRawY();
                if (!r.contains(rawX, rawY)) {
                    ((InputMethodManager) Objects.requireNonNull(this.getSystemService(Activity.INPUT_METHOD_SERVICE)))
                            .hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}