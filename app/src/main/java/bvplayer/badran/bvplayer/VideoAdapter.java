package bvplayer.badran.bvplayer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import bvplayer.badran.bvplayer.databinding.VideoItemBinding;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {
    Context mContext;
    static ArrayList<VideoFiles> videoFiles;
    LayoutInflater layoutInflater;

    public VideoAdapter(Context mContext, ArrayList<VideoFiles> videoFiles, LayoutInflater layoutInflater) {
        this.mContext = mContext;
        VideoAdapter.videoFiles = videoFiles;
        this.layoutInflater = layoutInflater;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(VideoItemBinding.inflate(layoutInflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.videoFileName.setText(videoFiles.get(position).getTitle());
        int mCurrentPosition = Integer.parseInt(videoFiles.get(position).getDuration()) / 1000;
        holder.binding.videoDuration.setText(formattedTime(mCurrentPosition));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.with(mContext)
                        .load(new File(videoFiles.get(holder.getBindingAdapterPosition()).getPath()))
                        .into(holder.binding.thumbnail);
            }
        });
        thread.start();
        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position", holder.getBindingAdapterPosition());
                intent.putExtra("sender", "FilesIsSending");
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoFiles.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        VideoItemBinding binding;

        public MyViewHolder(@NonNull VideoItemBinding b) {
            super(b.getRoot());
            binding = b;
        }
    }

    private String formattedTime(int mCurrentPosition) {
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        String totalOut = minutes + ":" + seconds;
        String totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }
    }

}
