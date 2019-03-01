package uk.ac.tees.newcomersmap;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserMarkerListAdapter extends ArrayAdapter<UserMarker> {

    public static final String TAG = "UserMarkerListAdapter";
    private final int resource;
    private View selectedView;

    private Context context;
    private Geocoder geocoder;

    public UserMarkerListAdapter(@NonNull Context context, int resource, @NonNull List<UserMarker> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    public void setGeocoder(Geocoder geocoder) {
        if(this.geocoder == null) {
            this.geocoder = geocoder;
        }
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UserMarker currentMarker = getItem(position);

        // Use Geocoder and reverse-geolocate nearest address
        String location;
        double latitude = currentMarker.getLocation().getLatitude();
        double longitude = currentMarker.getLocation().getLongitude();
        final List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(latitude,longitude,1);
            Address address = addressList.get(0);
            location = address.getAddressLine(0) + ", "
                    + address.getCountryCode();
        } catch (IOException e) {
            e.printStackTrace();
            location = "#geocoding error#";
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(resource, parent, false);


        TextView textViewTitle = convertView.findViewById(R.id.textView_marker_title);
        TextView textViewLocation = convertView.findViewById(R.id.textView_marker_location);

        textViewTitle.setText(currentMarker.getTitle());
        textViewLocation.setText(location);

        return convertView;
    }

}
