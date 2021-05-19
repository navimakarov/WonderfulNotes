package com.makarov.wonderfulnotes;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO add error textfields instead of snackbars
public class RegisterFragment extends Fragment {
    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        final EditText usernameEdit = root.findViewById(R.id.usernameRegister);
        final EditText emailEdit = root.findViewById(R.id.emailRegister);
        final EditText passwordEdit = root.findViewById(R.id.passwordRegister);
        AppCompatButton register = root.findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hide_keyboard();
                String email = emailEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();
                String username = usernameEdit.getText().toString().trim();

                String regex = "^(.+)@(.+)$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(email);

                if(!matcher.matches()) {
                    Snackbar errorSnackBar = Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Incorrect email!", Snackbar.LENGTH_LONG);
                    errorSnackBar.setTextColor(Color.RED);
                    errorSnackBar.show();
                    return;
                }
                if (TextUtils.isEmpty(username)) {
                    Snackbar errorSnackBar = Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Enter username!", Snackbar.LENGTH_LONG);
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

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Snackbar errorSnackBar = Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Authentication failed.", Snackbar.LENGTH_LONG);
                                    errorSnackBar.setTextColor(Color.RED);
                                    errorSnackBar.show();
                                } else {
                                    FirebaseUser user = auth.getCurrentUser();
                                    UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(username).build();
                                    user.updateProfile(profile);
                                    getActivity().recreate();
                                    getActivity().getSupportFragmentManager().beginTransaction()
                                            .replace(R.id.fragment_container, new HomeFragment()).commit();
                                    DrawerLayout dl = getActivity().findViewById(R.id.drawerLayout);
                                    dl.openDrawer(GravityCompat.START);
                                }
                            }
                        });
            }
        });
        return root;
    }

    private void hide_keyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

}
