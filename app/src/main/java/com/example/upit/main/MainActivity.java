package com.example.upit.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.upit.R;
import com.example.upit.data.Users;
import com.example.upit.databinding.ActivityMainBinding;
import com.example.upit.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject
    FirebaseAuth auth;

    private ActivityMainBinding binding;
    private NavController navController;
    private Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate with view binding
        // ViewBinding and DataBinding allows us to easily access views in the layout
        // with the syntax binding.viewId.
        // This removes the need for findViewById<Id> which can be messy.
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        // Store the user from the intent to a variable;
        user = (Users) getIntent().getSerializableExtra("current_user");

        // Set the content view
        setContentView(binding.getRoot());

        // Set up bottom navigation.
        // NavigationUI deals with attaching the navigation components, fragments, navigation graph,
        // menu, and bottom navigation view together thus making navigation easier.
        // This removes the need for manually managing fragments with FragmentManager
        // which can lead to a lot of overhead and lengthy code.
        // Read more: https://developer.android.com/guide/navigation/navigation-ui
        // Read more: https://developer.android.com/guide/navigation/navigation-getting-started
        navController = Navigation.findNavController(this, R.id.nav_host);
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        // Use the custom toolbar view in the main as the action bar
        // This allows us to use the methods that is normally only available for the actionbar
        setSupportActionBar(binding.toolbar);
    }

    // Will be called by the sign out item in the bottom navigation view
    public void signout(MenuItem menuItem) {
        // Sign out from firebase
        auth.signOut();

        // Finish the activity
        finishAffinity();

        // Start the login activity
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.action_bar_item, menu);

        // Add on destination change listener to toggle the action bar buttons
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Set default states of the action bar
            menu.findItem(R.id.add_course).setVisible(false);
            menu.findItem(R.id.add_student).setVisible(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);

            // Modify actionbar state based on the current destination
            switch(destination.getId()) {
                // If the current fragment is courseList and the user is a professor then
                // show the "+" icon for adding new courses
                case R.id.courseListFragment:
                    if(user.getAccountType().equals("PROFESSOR")) {
                        menu.findItem(R.id.add_course).setVisible(true);
                    }
                    break;
                // If the current fragment is studentFragment and the user is a professor then
                // show the "+" icon for adding new students
                case R.id.studentFragment:
                    if(user.getAccountType().equals("PROFESSOR")) {
                        menu.findItem(R.id.add_student).setVisible(true);
                    }
                    break;
                // If the fragment is not a top level destinations, then show the back arrow on the toolbar
                // Top-Level Destinations: CourseList, Student, Message
                case R.id.newStudentFragment: case R.id.courseDetailFragment:
                case R.id.messageFragment:
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                    break;
            }
        });

        return true;
    }

    /**
     * Needed for us to navigate up / go back when the back arrow on the toolbar is pressed
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    public Users getUser() {
        return user;
    }

    // Hide the keyboard when area outside of an edit text is clicked
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