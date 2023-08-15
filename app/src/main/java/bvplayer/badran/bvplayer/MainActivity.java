package bvplayer.badran.bvplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Objects;

import bvplayer.badran.bvplayer.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSION = 123;
    ActivityMainBinding binding;
    static ArrayList<VideoFiles> videoFiles = new ArrayList<>();
    static ArrayList<VideoFiles> albums = new ArrayList<>();
    static ArrayList<String> folderList = new ArrayList<>();
    String sender;
    static String myFolderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permission();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sender = getIntent().getStringExtra("sender");
        myFolderName = getIntent().getStringExtra("folderName");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (Objects.equals(sender, "folder")) {
        fragmentTransaction.replace(R.id.mineFragment, new FilesFragment());
        } else {
            fragmentTransaction.replace(R.id.mineFragment, new FolderFragment());
        }
        fragmentTransaction.commit();
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
        } else {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.mineFragment, new FolderFragment());
            fragmentTransaction.commit();
            videoFiles = getVideoFiles(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mineFragment, new FolderFragment());
                fragmentTransaction.commit();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
        }
    }

    private ArrayList<VideoFiles> getVideoFiles (Context context) {
        ArrayList<VideoFiles> tampVideoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media.ALBUM,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
        };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String id = cursor.getString(1);
                String path = cursor.getString(2);
                String title = cursor.getString(3);
                String size = cursor.getString(4);
                String dateAdded = cursor.getString(5);
                String duration = cursor.getString(6);

                VideoFiles videoFiles = new VideoFiles(id, path, title, size, dateAdded, duration, album);
                // /storage/sd_card/VideoDir/Abc/myVideoFile.mp4
                int slashFirstIndex = path.lastIndexOf("/");
                String subString = path.substring(0, slashFirstIndex);
                // /storage/sd_card/VideoDir/Abc because last index excluded so slash
                // excluded
                // after doing this it will give us "Abc" as a folder name
                if (!folderList.contains(subString)) {
                    folderList.add(subString);
                    Log.e("Theeeeeeeee ", "issssssss " + folderList);
                }
                tampVideoFiles.add(videoFiles);
            }
            cursor.close();
        }
        return tampVideoFiles;
    }
}