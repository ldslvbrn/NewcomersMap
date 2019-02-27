package uk.ac.tees.newcomersmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewcomerMapFragment extends Fragment {

    public static final String EXTRA_MAP_LIST_INDEX =
            "uk.ac.tees.newcomersmap.EXTRA_MAP_LIST_INDEX";

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG = "NewcomerMapFragment";
    private static int DEFAULT_ZOOM_LEVEL = 11;

    // View variables
    private Toolbar toolbar;
    private MapView mMapView;
    private GoogleMap gMap;
    private RecyclerView recyclerView;
    private MarkerRecyclerAdapter adapter;

    // Utils variables
    private Geocoder geocoder;
    private FusedLocationProviderClient fusedLocationClient;
    private NewcomerMapViewModel viewModel;
    private NewcomerMap newcomerMap;
    private WeakHashMap<UserMarker, Marker> markerHashMap = new WeakHashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_newcomer_map, container, false);

        // Set-up Toolbar
        toolbar = view.findViewById(R.id.toolbar_newcomer_map);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("");

        // Set-up ViewModel
        viewModel = ViewModelProviders.of(this)
                .get(NewcomerMapViewModel.class);

        // Get MapView
        mMapView = view.findViewById(R.id.mapView);
        mMapView.onCreate(null);

        // Step-up Recycler View
        recyclerView = view.findViewById(R.id.recycleListView_marker_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setHasFixedSize(true);

        geocoder = new Geocoder(getActivity());
        adapter = new MarkerRecyclerAdapter(geocoder);
        adapter.setOnItemClickListener(onItemClickListener);
        recyclerView.setAdapter(adapter);

        // TODO: Instance adapter, set OnButtonClickListeners and OnItemClickListener

        if (this.getArguments() != null) {
            newcomerMap = viewModel.getAllMaps().getValue()
                    .get(this.getArguments().getInt(EXTRA_MAP_LIST_INDEX));
            toolbar.setTitle(newcomerMap.getTitle());
            adapter.setMarkers(newcomerMap.getMarkers());

            // TODO fill out out marker RecyclerView fields

        } else {
            newcomerMap = new NewcomerMap();
            newcomerMap.setMarkers(new ArrayList<UserMarker>());
            showSetMapTitleDialog();
            toolbar.setTitle(newcomerMap.getTitle());
            adapter.setMarkers(newcomerMap.getMarkers());
        }

        // TODO: Save and cancel button
        // TODO: Edit title menu options

        // TODO: Override onBackPressed/OnNavigate
        // to change the title
        // to validate and prompt save dialog

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
        // Request Google API for a map
        mMapView.getMapAsync(onMapReadyCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void moveCamera(LatLng latLng) {
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                latLng, DEFAULT_ZOOM_LEVEL));
    }

    private void moveCamera(LatLng latLng, int zoom) {
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                latLng, zoom));
    }

    private void showSetMapTitleDialog() {
        final MapTitleDialog mapTitleDialog = new MapTitleDialog();
        mapTitleDialog.setMapTitleDialogListener(new MapTitleDialog.MapTitleDialogListener() {
            @Override
            public void OnDialogReturn(String title) {
                // Title is null if cancel button is pressed
                if (title == null || title.trim().length() < 3 || title.trim().length() > 16) {
                    Toast.makeText(getActivity(),
                            "The title can be between 3-16 characters long",
                            Toast.LENGTH_SHORT).show();

                } else {
                    newcomerMap.setTitle(title.trim());
                    toolbar.setTitle(newcomerMap.getTitle());
                }
            }
        });
        mapTitleDialog.show(getActivity().getSupportFragmentManager(),
                "Set Title Dialog");
    }

    private void showSetMarkerTitleDialog(final UserMarker marker) {
        final MarkerTitleDialog markerTitleDialog = new MarkerTitleDialog();
        markerTitleDialog.setMarkerTitleDialogListener(new MarkerTitleDialog.MarkerTitleDialogListener() {
            @Override
            public void OnDialogReturn(String title) {
                // Title is null if cancel button is pressed
                if (title == null || title.trim().length() < 1 || title.trim().length() > 16) {
                    Toast.makeText(getActivity(),
                            "The title can be between 3-16 characters long",
                            Toast.LENGTH_SHORT).show();
                } else {
                    marker.setTitle(title.trim());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        markerTitleDialog.show(getActivity().getSupportFragmentManager(),
                "Set Title Dialog");

    }

    // Set-up the Google once it's ready
    private final OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(final GoogleMap googleMap) {
            gMap = googleMap;
            // Enable map zoom buttons
            gMap.getUiSettings().setZoomControlsEnabled(true);
            gMap.getUiSettings().setCompassEnabled(true);

            // One more permission check
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                // Permission DENIED:
                moveCamera(new LatLng(49.085752, 7.570243), 15);
            } else {
                // Permissions OK:
                // Show and animate camera to the current location
                gMap.setMyLocationEnabled(true);
                gMap.getUiSettings().setMyLocationButtonEnabled(true);
                // Get current device location ASYNCHRONOUSLY
                if (getArguments() == null) {
                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                    final Task locationTask = fusedLocationClient.getLastLocation();
                    locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                Location location = task.getResult();
                                moveCamera(new LatLng(
                                        location.getLatitude(),
                                        location.getLongitude()));
                            }
                        }
                    });
                }
            }
            // Populate map with markers and store their reference
            if (newcomerMap != null) {
                // UserMarker tracking approach
                for (UserMarker userMarker : newcomerMap.getMarkers()) {
                    Marker mapMarker = gMap.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    userMarker.getLocation().getLatitude(),
                                    userMarker.getLocation().getLongitude()))
                            .title(userMarker.getTitle())
                            .snippet("Lat: " + userMarker.getLocation().getLatitude() +
                                    " Lng: " + userMarker.getLocation().getLongitude()));
                    mapMarker.setTag(userMarker);
                    markerHashMap.put(userMarker, mapMarker);
                }
//                // Index tracking approach
//                for (int index = 0; index >= newcomerMap.getMarkers().size(); index++) {
//                    UserMarker userMarker = newcomerMap.getMarkers().get(index);
//                    Marker mapMarker = gMap.addMarker(new MarkerOptions()
//                            .position(new LatLng(
//                                    userMarker.getLocation().getLatitude(),
//                                    userMarker.getLocation().getLongitude()))
//                            .title(userMarker.getTitle())
//                            .snippet("Lat: " + userMarker.getLocation().getLatitude() +
//                                    " Lng: " + userMarker.getLocation().getLongitude()));
//                    mapMarker.setTag(index);
//                }
            }
            // Set on MAP HOLD Listener
            gMap.setOnMapLongClickListener(onMapLongClickListener);
            // Set on MARKER TAP Listener
            gMap.setOnMarkerClickListener(onMarkerClickListener);
        }
    };

    private final MarkerRecyclerAdapter.OnItemClickListener onItemClickListener
            = new MarkerRecyclerAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(UserMarker marker) {
            Marker mapMarker = markerHashMap.get(marker);
            moveCamera(mapMarker.getPosition());
            mapMarker.showInfoWindow();
        }
    };

    private final GoogleMap.OnMapLongClickListener onMapLongClickListener
            = new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            // TODO: dialog pop up
            UserMarker userMarker = new UserMarker();
            userMarker.setLocation(new GeoPoint(latLng.latitude, latLng.longitude));
            showSetMarkerTitleDialog(userMarker);
            newcomerMap.getMarkers().add(userMarker);
            Marker mapMarker = gMap.addMarker(new MarkerOptions()
                    .position(new LatLng(
                            userMarker.getLocation().getLatitude(),
                            userMarker.getLocation().getLongitude()))
                    .title(userMarker.getTitle())
                    .snippet("Lat: " + userMarker.getLocation().getLatitude() +
                            " Lng: " + userMarker.getLocation().getLongitude()));
            mapMarker.setTag(userMarker);
            markerHashMap.put(userMarker, mapMarker);
            adapter.notifyItemInserted(newcomerMap.getMarkers().indexOf(userMarker));
        }
    };

    private final GoogleMap.OnMarkerClickListener onMarkerClickListener
            = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            int markerIndex = newcomerMap.getMarkers().indexOf(marker.getTag());
            // Scroll item 2 to 20 pixels from the top
            // linearLayoutManager.scrollToPositionWithOffset(2, 20);

            // this fucks up
            recyclerView.scrollToPosition(markerIndex);
            recyclerView.findViewHolderForAdapterPosition(markerIndex)
                    .itemView.performClick();

//            // this doesnt
//            RecyclerView.ViewHolder viewHolder = recyclerView
//                    .findViewHolderForAdapterPosition(markerIndex);
//            if (viewHolder != null) {
//                recyclerView.scrollToPosition(markerIndex);
//                viewHolder.itemView.performClick();
//
//            } else {
//                adapter.notifyItemChanged(markerIndex);
//            }


            return false;
        }
    };

}
