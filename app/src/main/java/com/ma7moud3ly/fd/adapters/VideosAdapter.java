package com.ma7moud3ly.fd.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.ma7moud3ly.fd.observers.VideoObserver;
import com.ma7moud3ly.fd.databinding.ItemVideoBinding;
import com.ma7moud3ly.fd.downloader.SavedVideo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.MyViewHolder> {
    private final List<SavedVideo> list;
    private final VideoObserver observer;

    public VideosAdapter(List<SavedVideo> list, VideoObserver observer) {
        this.list = list;
        this.observer=observer;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ItemVideoBinding binding;

        public MyViewHolder(ItemVideoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVideoBinding binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (list != null && position < list.size()) {
            SavedVideo video = list.get(position);
            holder.binding.setVideo(video);
            holder.binding.setObserver(observer);
        }
    }

    @NonNull
    @Override
    public int getItemCount() {
        return list.size();
    }


}
