package bvplayer.badran.bvplayer;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import bvplayer.badran.bvplayer.databinding.VideoItemBinding;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.MyHolder> {
    static ArrayList<VideoFiles> folderVideoFile;
    Context mContext;
    LayoutInflater layoutInflater;
    final String myFolderName;

    public VideoFolderAdapter(ArrayList<VideoFiles> folderVideoFile, Context mContext, LayoutInflater layoutInflater, String myFolderName) {
        VideoFolderAdapter.folderVideoFile = folderVideoFile;
        this.mContext = mContext;
        this.layoutInflater = layoutInflater;
        this.myFolderName = myFolderName;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(VideoItemBinding.inflate(layoutInflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.binding.videoFileName.setText(folderVideoFile.get(position).getTitle());
        if (!TextUtils.isEmpty(folderVideoFile.get(position).getDuration()) && TextUtils.isDigitsOnly(folderVideoFile.get(position).getDuration())) {
            int mCurrentDuration = Integer.parseInt(folderVideoFile.get(position).getDuration()) / 1000;
            holder.binding.videoDuration.setText(formattedTime(mCurrentDuration));
        }
        Glide.with(mContext)
                .load(new File(folderVideoFile.get(position).getPath()))
                .into(holder.binding.thumbnail);
        holder.binding.getRoot().setOnClickListener(v -> {
            Intent intent = new Intent(mContext, PlayerActivity.class);
            intent.putExtra("sender", myFolderName);
            intent.putExtra("position", position);
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return folderVideoFile.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        VideoItemBinding binding;

        public MyHolder(@NonNull VideoItemBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    private String formattedTime(int mCurrentPosition) {
        int seconds = mCurrentPosition % 60;
        double dHour = (mCurrentPosition / 60f) / 60.0;
        int hour = (int) dHour;
        int minutes = (int) ((dHour - hour) * 60);
        String s, m, h;
        if (seconds < 10) {
            s = "0" + seconds;
        } else {
            s = String.valueOf(seconds);
        }
        if (minutes < 10) {
            m = "0" + minutes;
        } else {
            m = String.valueOf(minutes);
        }
        if (hour == 0) {
            return m + ":" + s;
        } else {
            h = String.valueOf(hour);
            return h + ":" + m + ":" + s;
        }

    }

}
