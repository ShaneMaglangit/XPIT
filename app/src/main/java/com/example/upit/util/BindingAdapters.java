package com.example.upit.util;

import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import org.w3c.dom.Text;

public class BindingAdapters {

    /**
     * A binding adapter that creates a custom xml attribute that allows
     * us to easily append the professor suffix to any given string
     * Usage: In the layout xml (fragment_course_detail) app:professor="@{viewModel.course.professor}"
     * @param view
     * @param name
     */
    @BindingAdapter("professor")
    public static void setProfessorName(TextView view, String name) {
        if(name != null) {
            view.setText("Professor " + name);
        }
    }
}
