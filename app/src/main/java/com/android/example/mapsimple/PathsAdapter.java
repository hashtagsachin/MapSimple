package com.android.example.mapsimple;

import android.graphics.PointF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.example.mapsimple.model.PathInfo;

import java.util.List;
import java.util.Locale;

public class PathsAdapter extends RecyclerView.Adapter<PathsAdapter.PathViewHolder> {
    private List<PathInfo> pathList;
    private OnItemLongClickListener longClickListener;


    public interface OnItemLongClickListener {
        void onItemLongClicked(String documentId);
    }

    public PathsAdapter(List<PathInfo> pathList, OnItemLongClickListener longClickListener) {
        this.pathList = pathList;
        this.longClickListener = longClickListener;
    }


    @NonNull
    @Override
    public PathViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.path_info_item, parent, false);
        return new PathViewHolder(v, longClickListener); // Pass the listener to the constructor
    }

    @Override
    public void onBindViewHolder(@NonNull PathViewHolder holder, int position) {
        PathInfo path = pathList.get(position);
        holder.currentItem = path; // Set the current item here
        holder.startLocationView.setText(path.getStartLocation());
        holder.endLocationView.setText(path.getEndLocation());
        holder.nodeCoordinatesView.setText(getCoordinatesString(path.getNodeCoordinates()));
    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }

    // Method to update the dataset and refresh the RecyclerView
    public void setPaths(List<PathInfo> newPathList) {
        this.pathList = newPathList;
        notifyDataSetChanged();
    }

    private String getCoordinatesString(List<PointF> coordinates) {
        StringBuilder sb = new StringBuilder();
        for (PointF point : coordinates) {
            sb.append(String.format(Locale.US, "(%.2f,%.2f) ", point.x, point.y));
        }
        return sb.toString().trim();
    }

    static class PathViewHolder extends RecyclerView.ViewHolder {
        TextView startLocationView, endLocationView, nodeCoordinatesView;
        PathInfo currentItem;

        PathViewHolder(View itemView, OnItemLongClickListener longClickListener) {
            super(itemView);
            startLocationView = itemView.findViewById(R.id.startLocationTextView);
            endLocationView = itemView.findViewById(R.id.endLocationTextView);
            nodeCoordinatesView = itemView.findViewById(R.id.nodeCoordinatesTextView);

            itemView.setOnLongClickListener(v -> {
                if (currentItem != null && longClickListener != null) {
                    longClickListener.onItemLongClicked(currentItem.getDocumentId()); // Use currentItem here
                    return true;
                }
                return false;
            });
        }
    }

}
