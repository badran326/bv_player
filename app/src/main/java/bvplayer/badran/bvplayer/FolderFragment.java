package bvplayer.badran.bvplayer;

import static bvplayer.badran.bvplayer.MainActivity.albums;
import static bvplayer.badran.bvplayer.MainActivity.folderList;
import static bvplayer.badran.bvplayer.MainActivity.videoFiles;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class FolderFragment extends Fragment {

    View view;
    RecyclerView recyclerView;
    FolderAdapter folderAdapter;

    public FolderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_folder, container, false);
        recyclerView = view.findViewById(R.id.folderRV);
        if (folderList != null && folderList.size() > 0 && videoFiles != null) {
            folderAdapter = new FolderAdapter( videoFiles, getContext(), getLayoutInflater(), folderList);
            recyclerView.setAdapter(folderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        return view;
    }
}