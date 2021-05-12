package com.makarov.wonderfulnotes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    private NotesRecViewAdapter adapter;
    private ArrayList<Note> notes = new ArrayList<>();

    private static final int STORAGE_PERMISSION_CODE = 0;

    private SQLiteDatabase db;
    private DrawerLayout drawerLayout;
    // TODO icon when no notes found
    // TODO add logs
    // TODO ask for storage permission

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
                    case R.id.home:
                        break;
                    case R.id.import_item:
                        importDB();
                        break;
                    case R.id.export_item:
                        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                        // Grant access and export database if access is granted
                        break;
                    case R.id.cloud_item:
                        break;
                        //TODO
                    case R.id.settings_item:
                        //TODO
                        break;
                    case R.id.about_item:
                        //TODO
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
        RecyclerView notesRecView = findViewById(R.id.notesRecView);
        ExtendedFloatingActionButton newNoteButton = findViewById(R.id.newNoteButton);

        db = getBaseContext().openOrCreateDatabase("notes.db", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS notes (id INTEGER PRIMARY KEY, date TEXT, title TEXT, note TEXT, highlight INTEGER);");
        read_from_db();

        Intent intent = getIntent();
        if (intent.hasExtra("error")) {
            String error = intent.getStringExtra("error");
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }

        newNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                startActivity(intent);
                finish();
            }
        });

        adapter = new NotesRecViewAdapter();
        adapter.setNotes(notes);

        notesRecView.setAdapter(adapter);
        notesRecView.setLayoutManager(new LinearLayoutManager(this));

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                db.delete("notes", "id=" + viewHolder.itemView.getTag(), null);
                read_from_db();
                adapter.notifyItemRemoved(viewHolder.getLayoutPosition());
            }

        }).attachToRecyclerView(notesRecView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == RESULT_OK) {
            assert data != null;
            Uri selectedFile = data.getData();
            assert selectedFile != null;
            openDB_from_file(selectedFile);
        }
    }

    private void read_from_db() {
        Cursor query = db.rawQuery("SELECT * FROM notes;", null);

        notes.clear();
        if(query.moveToFirst()){
            do{
                int id = query.getInt(0);
                String date = query.getString(1);
                String title = query.getString(2);
                String text = query.getString(3);
                int highlight = query.getInt(4);

                Note note = new Note(date, title, text, id);
                if(highlight == 1){
                    note.highlight();
                }
                notes.add(note);
            }
            while(query.moveToNext());
        }
        Collections.reverse(notes);
        query.close();
    }

    public void exportDB() {
        try {
            checkAppDir();
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            String  currentDBPath= "//data//" + "com.makarov.wonderfulnotes"
                    + "//databases//" + "notes.db";
            @SuppressLint("SimpleDateFormat") String backupDBName = new SimpleDateFormat("ddMMyyHHmmss").format(new Date());
            backupDBName = "notes";
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
                read_from_db();
                adapter.notifyDataSetChanged();

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

    private void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
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
}