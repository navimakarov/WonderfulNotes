package com.makarov.wonderfulnotes;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO add error textfields instead of snackbars
public class LoginFragment extends Fragment {
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        final EditText emailEdit = root.findViewById(R.id.emailLogin);
        final EditText passwordEdit = root.findViewById(R.id.passwordLogin);

        AppCompatButton login = root.findViewById(R.id.loginButton);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();

                String regex = "^(.+)@(.+)$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(email);

                if(!matcher.matches()) {
                    Snackbar errorSnackBar = Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Incorrect email!", Snackbar.LENGTH_LONG);
                    errorSnackBar.setTextColor(Color.RED);
                    errorSnackBar.show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Snackbar errorSnackBar = Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Enter email!", Snackbar.LENGTH_LONG);
                    errorSnackBar.setTextColor(Color.RED);
                    errorSnackBar.show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Snackbar errorSnackBar = Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Enter password!", Snackbar.LENGTH_LONG);
                    errorSnackBar.setTextColor(Color.RED);
                    errorSnackBar.show();
                    return;
                }
                if (password.length() < 6) {
                    Snackbar errorSnackBar = Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Password too short, enter minimum 6 characters!", Snackbar.LENGTH_LONG);
                    errorSnackBar.setTextColor(Color.RED);
                    errorSnackBar.show();
                    return;
                }

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Snackbar errorSnackBar = Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Login failed.", Snackbar.LENGTH_LONG);
                                    errorSnackBar.setTextColor(Color.RED);
                                    errorSnackBar.show();
                                } else {
                                    Snackbar successSnackBar = Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Login completed.", Snackbar.LENGTH_LONG);
                                    successSnackBar.show();
                                }
                            }
                        });
            }
        });

        AppCompatButton register = root.findViewById(R.id.registerFragmentButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RegisterFragment()).addToBackStack("Login").commit();
            }
        });
        return root;
    }
}
