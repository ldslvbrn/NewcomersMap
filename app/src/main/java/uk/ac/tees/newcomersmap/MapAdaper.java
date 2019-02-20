package uk.ac.tees.newcomersmap.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import uk.ac.tees.newcomersmap.R;
import uk.ac.tees.newcomersmap.data.NewcomerMap;

public class MapAdaper extends RecyclerView.Adapter<MapAdaper.MapHolder> {

    private List<NewcomerMap> maps = new ArrayList<>();

    @NonNull
    @Override
    public MapHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MapHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
            // TODO: Use Geolocator to generate town
        }
    }
}
