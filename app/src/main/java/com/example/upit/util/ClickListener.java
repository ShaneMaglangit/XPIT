package com.example.upit.util;

/**
 * An interface that allows us to attach click listeners on
 * recyclerview adapters easily
 */
public interface ClickListener {
    // The data type <T> is a generic thus allowing us to pass any data type
    // from the adapter back to the fragment / activity
    public <T> void onClick(T item);
}