package com.makarov.wonderfulnotes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    private static final int STORAGE_PERMISSION_CODE = 0;
    private static final int IMAGE_PICK_CODE = 1;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView profilePic;
    private  FirebaseAuth auth;
    private boolean signedOut;
    private StorageReference storageRef;
    // TODO icon when no notes found
    // TODO add logs
    // TODO ask for storage permission
    // TODO fix import/export

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();
        signedOut = false;

        drawerLayout = findViewById(R.id.drawerLayout);
        if(savedInstanceState != null) {
            boolean signedOutSavedInstance = savedInstanceState.getBoolean("SignOut");
            if(signedOutSavedInstance) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        }
        navigationView = findViewById(R.id.navigationView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_item:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new HomeFragment()).commit();
                        break;
                    case R.id.import_item:
                        importDB();
                        break;
                    case R.id.export_item:
                        checkPermission();
                        // Grant access and export database if access is granted
                        break;
                    case R.id.cloud_import_item:
                        importFromCloud();
                        break;
                        //TODO
                    case R.id.cloud_export_item:
                        exportToCloud();
                        break;
                    case R.id.settings_item:
                        //TODO
                        break;
                    case R.id.about_item:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new AboutFragment()).commit();
                        break;
                    case R.id.sign_out_item:
                        if (auth.getCurrentUser() != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                            builder.setTitle("Sign out");
                            builder.setMessage("Are you sure you want to sign out?");

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseAuth.getInstance().signOut();
                                    signedOut = true;
                                    recreate();
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    drawerLayout.openDrawer(GravityCompat.START);
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        else {
                            showError("Not signed in");
                        }
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        findViewById(R.id.imageMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.home_item);
        }

    }

    private void importFromCloud() {
        Snackbar cloudImportDoneSnackbar = Snackbar.make(drawerLayout, "Exported notes to cloud", Snackbar.LENGTH_LONG);
        cloudImportDoneSnackbar.setTextColor(Color.YELLOW);
        cloudImportDoneSnackbar.show();
    }

    private void exportToCloud() {
        FirebaseUser user = auth.getCurrentUser();
        if(user != null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users/" + user.getUid() + "/DB");
            SQLiteDatabase db = getBaseContext().openOrCreateDatabase("notes.db", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, date TEXT, title TEXT, note TEXT, highlight INTEGER);");
            ArrayList<Note> notes = new ArrayList<>();
            DataBase.read_from_db(db, notes);
            for (Note note : notes) {
                ref.push().setValue(note);
            }

            Snackbar cloudExportDoneSnackbar = Snackbar.make(drawerLayout, "Exported notes to cloud", Snackbar.LENGTH_LONG);
            cloudExportDoneSnackbar.setTextColor(Color.YELLOW);
            cloudExportDoneSnackbar.show();

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("SignOut", signedOut);
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState, @NonNull @NotNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        FirebaseUser user = auth.getCurrentUser();
        Menu menu = navigationView.getMenu();
        MenuItem cloud = menu.findItem(R.id.cloud_item);
        if (user != null) {
            navigationView.inflateHeaderView(R.layout.layout_navigation_header_signed_in);
            View headerView = navigationView.getHeaderView(0);
            profilePic = headerView.findViewById(R.id.profileImage);
            TextView emailNav = headerView.findViewById(R.id.emailNav);
            TextView usernameNav = headerView.findViewById(R.id.usernameNav);

            String email = user.getEmail();
            String username = user.getDisplayName();

            emailNav.setText(email);
            usernameNav.setText(username);

            StorageReference profileRef = storageRef.child("users/" + auth.getCurrentUser().getUid() + "/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profilePic);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    profilePic.setImageResource(R.drawable.profile_icon);
                }
            });
            cloud.setVisible(true);
        }
        else {
            navigationView.inflateHeaderView(R.layout.layout_navigation_header);
            cloud.setVisible(false);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    public void exportDB() {
        try {
            checkAppDir();
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            String  currentDBPath= "//data//" + "com.makarov.wonderfulnotes"
                    + "//databases//" + "notes.db";
            @SuppressLint("SimpleDateFormat") String backupDBName = new SimpleDateFormat("ddMMyyHHmmss").format(new Date());
            String backupDBPath  = "//WonderfulNotes//" + backupDBName + ".db";
            File currentDB = new File(data, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();

            Snackbar exportDoneSnackbar = Snackbar.make(drawerLayout, "Exported to /WonderfulNotes folder", Snackbar.LENGTH_LONG);
            exportDoneSnackbar.setAction("OPEN", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO add file explorer open
                }
            });
            exportDoneSnackbar.setDuration(5000);
            exportDoneSnackbar.setTextColor(Color.YELLOW);
            exportDoneSnackbar.show();

        } catch (Exception e) {
            showError("Error: Export failed!");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void importDB() {
        Intent openFile = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(openFile, "Select a .db file"), 0);
    }

    private void openDB_from_file(Uri dbUri) {
        String dbPath = dbUri.getPath();
        assert dbPath != null;
        String extension = dbPath.substring(dbPath.lastIndexOf("."));
        if(extension.equals(".db")) {
            try {
                InputStream inputStream = getContentResolver().openInputStream(dbUri);
                File data = Environment.getDataDirectory();
                String  currentDBPath= "//data//" + "com.makarov.wonderfulnotes"
                        + "//databases//" + "notes.db";
                File currentDB = new File(data, currentDBPath);
                copyInputStreamToFile(inputStream, currentDB);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment()).commit(); // update data

                Snackbar importDoneSnackbar = Snackbar.make(drawerLayout, "Imported notes", Snackbar.LENGTH_LONG);
                importDoneSnackbar.setTextColor(Color.YELLOW);
                importDoneSnackbar.show();
            } catch (FileNotFoundException e) {
                showError("Error: Import failed!");
            }
        }
    }

    private void checkAppDir() {
        File dir = new File(Environment.getExternalStorageDirectory() + "//WonderfulNotes//");

        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private void copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
        }
        catch (Exception e) {
            showError("Error: Import failed!");
        }
        finally {
            try {
                if ( out != null ) {
                    out.close();
                }
                in.close();
            }
            catch ( IOException e ) {
                showError("Error: Import failed!");
            }
        }
    }

    private void showError(String message) {
        Snackbar error = Snackbar.make(drawerLayout, message, Snackbar.LENGTH_LONG);
        error.setAction("Check logs", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO add Open Logs folder
            }
        });
        error.setTextColor(Color.RED);
        error.show();
    }

    private void checkPermission()
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MainActivity.STORAGE_PERMISSION_CODE);
        }
        else {
            exportDB();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportDB();
            } else {
                showError("Access to Storage was not granted");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == STORAGE_PERMISSION_CODE && resultCode == RESULT_OK) {
            assert data != null;
            Uri selectedFile = data.getData();
            assert selectedFile != null;
            openDB_from_file(selectedFile);
        }

        else if(requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            uploadImageToFirebase(imageUri);
        }
    }

    public void loginOnClick(View view) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment()).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void loadPic(View view) {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, IMAGE_PICK_CODE);
    }

    private void uploadImageToFirebase(Uri imageUri) {
        StorageReference fileRef = storageRef.child("users/" + auth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Snackbar imageLoadDoneSnackbar = Snackbar.make(drawerLayout, "Image was uploaded!", Snackbar.LENGTH_LONG);
                imageLoadDoneSnackbar.setDuration(5000);
                imageLoadDoneSnackbar.setTextColor(Color.YELLOW);
                imageLoadDoneSnackbar.show();
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profilePic);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        showError("Failed to load an image!");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                showError("Failed to load an image!");
            }
        });
    }

}