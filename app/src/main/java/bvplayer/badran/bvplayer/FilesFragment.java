package bvplayer.badran.bvplayer;

import static bvplayer.badran.bvplayer.MainActivity.myFolderName;
import static bvplayer.badran.bvplayer.VideoFolderActivity.videoFilesArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class FilesFragment extends Fragment {

    RecyclerView recyclerView;
    View view;
    VideoFolderAdapter videoFolderAdapter;
    ImageView imageView;
    TextView textView;
    FloatingActionButton floatingActionButton;

    public FilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("STEEP", "Steep fragment 1 " + myFolderName);
        view = inflater.inflate(R.layout.fragment_files, container, false);
        recyclerView = view.findViewById(R.id.videoFolderRV);
        imageView = view.findViewById(R.id.back_btn);
        imageView.setOnClickListener(view -> requireActivity().onBackPressed());
        int index = myFolderName.lastIndexOf("/");
        String folder = myFolderName.substring(index + 1);
        textView = view.findViewById(R.id.title);
        textView.setText(folder);
        floatingActionButton = view.findViewById(R.id.floatingButton);
        floatingActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), PlayerActivity.class);
            intent.putExtra("sender", myFolderName);
            startActivity(intent);
        });

        if (myFolderName != null) {
            videoFilesArrayList = getVideoFiles(requireContext(), myFolderName);
        }
        if ( videoFilesArrayList.size() > 0 ) {
            videoFolderAdapter = new VideoFolderAdapter(videoFilesArrayList, getContext(), getLayoutInflater(), myFolderName);
            recyclerView.setAdapter(videoFolderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        // Inflate the layout for this fragment
        return view;
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