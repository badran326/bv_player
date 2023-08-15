package bvplayer.badran.bvplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import bvplayer.badran.bvplayer.databinding.FolderItemBinding;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {
    static ArrayList<VideoFiles> videoFolder;
    Context mContext;
    LayoutInflater layoutInflater;
    private final ArrayList<String> folderName;

    public FolderAdapter(ArrayList<VideoFiles> videoFolder, Context mContext, LayoutInflater layoutInflater, ArrayList<String> folderName) {
        FolderAdapter.videoFolder = videoFolder;
        this.mContext = mContext;
        this.layoutInflater = layoutInflater;
        this.folderName = folderName;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(FolderItemBinding.inflate(layoutInflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int index = folderName.get(position).lastIndexOf("/");
        String folder = folderName.get(position).substring(index + 1);
        holder.binding.folderName.setText(folder);
        holder.binding.countFilesFolder.setText(NumberOfFiles(folder));
        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(mContext, MainActivity.class);
            intent.putExtra("folderName", folderName.get(position));
            intent.putExtra("sender", "folder");
            intent.putExtra("position", position);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return folderName.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        FolderItemBinding binding;
        public MyViewHolder(@NonNull FolderItemBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    String NumberOfFiles (String folderName) {
        int countFiles = 0;
        for (VideoFiles videoFiles : videoFolder) {
            if (videoFiles.getPath()
                    .substring(0, videoFiles.getPath().lastIndexOf("/"))
                    .endsWith(folderName)){
                countFiles ++;
            }
        }
        return countFiles + " media files";
    }
}
