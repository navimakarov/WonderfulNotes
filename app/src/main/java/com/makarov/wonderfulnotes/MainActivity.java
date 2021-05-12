package com.makarov.wonderfulnotes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    private static final int STORAGE_PERMISSION_CODE = 0;
    private DrawerLayout drawerLayout;
    // TODO icon when no notes found
    // TODO add logs
    // TODO ask for storage permission
    // TODO fix import/export

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
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
                    case R.id.cloud_item:
                        break;
                        //TODO
                    case R.id.settings_item:
                        //TODO
                        break;
                    case R.id.about_item:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new AboutFragment()).commit();
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

        Intent intent = getIntent();
        if (intent.hasExtra("error")) {
            String error = intent.getStringExtra("error");
            showError(error);
        }

        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.home_item);
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

                //TODO
                //read_from_db();
                //adapter.notifyDataSetChanged();

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
        if(requestCode == 0 && resultCode == RESULT_OK) {
            assert data != null;
            Uri selectedFile = data.getData();
            assert selectedFile != null;
            openDB_from_file(selectedFile);
        }
    }
}