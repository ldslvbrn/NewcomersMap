package uk.ac.tees.newcomersmap;

import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MarkerRecyclerAdapter extends RecyclerView.Adapter<MarkerRecyclerAdapter.MarkHolder> {

    private List<UserMarker> markers = new ArrayList<>();
    private Geocoder geocoder;
    private OnButtonClickListener onEditPressedListener, onDeletePressedListener;

    public MarkerRecyclerAdapter(Geocoder geocoder) {
        super();
        this.geocoder = geocoder;
    }

    @NonNull
    @Override
    public MarkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_marker, parent, false);
        return new MarkHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkHolder holder, int position) {
        UserMarker currentMarker = markers.get(position);
        holder.textViewTitle.setText(currentMarker.getTitle());

        // Use Geocoder and reverse-geolocate nearest address
        double latitude = currentMarker.getLocation().getLatitude();
        double longitude = currentMarker.getLocation().getLongitude();
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(latitude,longitude,1);
            Address address = addressList.get(0);
            String location = address.getAddressLine(0) + ", "
                    + address.getCountryCode();
            holder.textViewLocation.setText(location);
        } catch (IOException e) {
            e.printStackTrace();
            holder.textViewLocation.setText("geocoding error");
        }
    }

    @Override
    public int getItemCount() {
        return markers.size();
    }

    public void setMarkers(List<UserMarker> markers) {
        this.markers = markers;
        notifyDataSetChanged();
    }

    public void setOnEditPressedListener(OnButtonClickListener listener) {
        this.onEditPressedListener = listener;
    }

    public void setOnDeletePressedListener(OnButtonClickListener onDeletePressedListener) {
        this.onDeletePressedListener = onDeletePressedListener;
    }

    class MarkHolder extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewLocation;
        private Button buttonEdit;
        private Button buttonDelete;

        public MarkHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textView_marker_title);
            textViewLocation = itemView.findViewById(R.id.textView_marker_location);
            buttonEdit = itemView.findViewById(R.id.button_edit_marker);
            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onEditPressedListener != null && position != RecyclerView.NO_POSITION) {
                        onEditPressedListener.onButtonClick(markers.get(position));
                    }
                }
            });
            buttonDelete = itemView.findViewById(R.id.button_edit_marker);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (onDeletePressedListener != null && position != RecyclerView.NO_POSITION) {
                        onDeletePressedListener.onButtonClick(markers.get(position));
                    }
                }
            });
        }
    }

    protected interface OnButtonClickListener {
        void onButtonClick(UserMarker marker);
    }
}
