package uk.ac.tees.newcomersmap.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import uk.ac.tees.newcomersmap.R;
import uk.ac.tees.newcomersmap.NewcomerMap;
import uk.ac.tees.newcomersmap.UserMarker;

public class MapAdaper extends RecyclerView.Adapter<MapAdaper.MapHolder> {

    private List<NewcomerMap> maps = new ArrayList<>();

    @NonNull
    @Override
    public MapHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ncmap, parent, false);
        return new MapHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MapHolder holder, int position) {
        NewcomerMap currentMap = maps.get(position);
        holder.textViewTitle.setText(currentMap.getTitle());
        int markerCount = 0;
        for (UserMarker marker : maps.get(position).getMarkers()) {
            markerCount++;
        }
        holder.textViewMarkers.setText(markerCount);

        // TODO: Use Geolocator to get town or area near the coordinates
        //String location =  DO MAGIC HERE!
        //holder.textViewLocation.setText(location);
    }

    @Override
    public int getItemCount() {
        return maps.size();
    }

    public void setMaps(List<NewcomerMap> maps) {
        this.maps = maps;
        notifyDataSetChanged();
    }

    class MapHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewLocation;
        private TextView textViewMarkers;

        public MapHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textView_title);
            textViewLocation = itemView.findViewById(R.id.textView_location);
            textViewMarkers = itemView.findViewById(R.id.textView_markers);
        }
    }
}
