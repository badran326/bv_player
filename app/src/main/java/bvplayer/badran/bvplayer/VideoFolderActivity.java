package bvplayer.badran.bvplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.util.ArrayList;

import bvplayer.badran.bvplayer.databinding.ActivityVideoFolderBinding;

public class VideoFolderActivity extends AppCompatActivity {

    ActivityVideoFolderBinding binding;
    VideoFolderAdapter videoFolderAdapter;
    public static ArrayList<VideoFiles> videoFilesArrayList = new ArrayList<>();
    String myFolderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVideoFolderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myFolderName = getIntent().getStringExtra("folderName");
        String goToPlayActivity = getIntent().getStringExtra("PlayerActivity");
//        if (myFolderName != null){
//            int j = 0;
//            for (int i = 0 ; i < videoFiles.size(); i++) {
//                if (myFolderName.equals(videoFiles.get(i).getAlbum())) {
//                    videoFilesArrayList.add(j, videoFiles.get(i));
//                    j ++;
//                }
//            }
//        }
        if (myFolderName != null) {
            videoFilesArrayList = getVideoFiles(this, myFolderName);
        }
        if ( videoFilesArrayList.size() > 0 ) {
            videoFolderAdapter = new VideoFolderAdapter(videoFilesArrayList, this, getLayoutInflater(), myFolderName);
            binding.videoFolderRV.setAdapter(videoFolderAdapter);
            binding.videoFolderRV.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.floatingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("sender", myFolderName);
            startActivity(intent);
        });
    }

    private ArrayList<VideoFiles> getVideoFiles (Context context, String folderName) {
        ArrayList<VideoFiles> tampVideoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.Media.ALBUM,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION
        };
        String selection = MediaStore.Video.Media.DATA + " like?";
        String[] selectionArgs = new String[]{"%" + folderName + "%"};
        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null, null);
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
                int slashFirstIndex = path.lastIndexOf("/");
                if (videoFiles.getPath().substring(0, slashFirstIndex).equals(folderName)) {
                    tampVideoFiles.add(videoFiles);
                }
            }
            cursor.close();
        }
        return tampVideoFiles;
    }
}