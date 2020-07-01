package com.example.upit.di;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;

import androidx.viewbinding.ViewBinding;

import com.example.upit.data.Users;
import com.example.upit.databinding.ActivityLoginBinding;
import com.example.upit.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.components.ApplicationComponent;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Module
@InstallIn(ApplicationComponent.class)
public class HiltModule {
    @Provides
    @Singleton
    public static FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    public static FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public static FirebaseStorage provideFirebaseStorage() { return FirebaseStorage.getInstance(); }

    @Provides
    @Singleton
    public static DownloadManager provideDownloadManager(@ApplicationContext Context context) {
        return (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }
}
