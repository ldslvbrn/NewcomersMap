package uk.ac.tees.newcomersmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.WeakHashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

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
    private Toolbar mToolbar;
    private MapView mMapView;
    private UserMarkerListAdapter mMarkerListAdapter;
     private ListView mListView;

    // Utils variables
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private NewcomerMapViewModel viewModel;
    private NewcomerMap newcomerMap;
    private WeakHashMap<UserMarker, Marker> mMarkerHashMap = new WeakHashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_newcomer_map, container, false);

        if(savedInstanceState == null) {
            // Acquire ViewModel
            viewModel = ViewModelProviders.of(this)
                    .get(NewcomerMapViewModel.class);

            // Set-up ListView
            mListView = view.findViewById(R.id.ListView_marker_list);

            // Set-up Toolbar
            mToolbar = view.findViewById(R.id.toolbar_newcomer_map);
            ((MainActivity) getActivity()).setSupportActionBar(mToolbar);
            mToolbar.setTitle("");

            if (this.getArguments() != null) {
                newcomerMap = viewModel.getAllMaps().getValue()
                        .get(this.getArguments().getInt(EXTRA_MAP_LIST_INDEX));
                mToolbar.setTitle(newcomerMap.getTitle());
                mMarkerListAdapter = new UserMarkerListAdapter(getActivity(),
                        R.layout.item_user_marker,newcomerMap.getMarkers());
                mMarkerListAdapter.setGeocoder(new Geocoder(getActivity()));
            } else {
                newcomerMap = new NewcomerMap();
                newcomerMap.setMarkers(new ArrayList<UserMarker>());
                showSetMapTitleDialog();
                mToolbar.setTitle(newcomerMap.getTitle());
                mMarkerListAdapter = new UserMarkerListAdapter(getActivity(),
                        R.layout.item_user_marker,newcomerMap.getMarkers());
                mMarkerListAdapter.setGeocoder(new Geocoder(getActivity()));
            }

            mListView.setAdapter(mMarkerListAdapter);
            mListView.setOnItemClickListener(onItemClickListener);

            // Get MapView
            mMapView = view.findViewById(R.id.mapView);
            mMapView.onCreate(null);

            // TODO: Instance adapter, set OnButtonClickListeners

            // TODO: Save and cancel button
            // TODO: Edit title menu options

            // TODO: Override onBackPressed/OnNavigate
            // to change the title
            // to validate and prompt save dialog
        }


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
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                latLng, DEFAULT_ZOOM_LEVEL));
    }

    private void moveCamera(LatLng latLng, int zoom) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
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
                    mToolbar.setTitle(newcomerMap.getTitle());
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
                    mMarkerListAdapter.notifyDataSetChanged();
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
            mGoogleMap = googleMap;
            // Enable map zoom buttons
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setCompassEnabled(true);

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
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                // Get current device location ASYNCHRONOUSLY
                if (getArguments() == null) {
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                    final Task locationTask = mFusedLocationClient.getLastLocation();
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
                    Marker mapMarker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    userMarker.getLocation().getLatitude(),
                                    userMarker.getLocation().getLongitude()))
                            .title(userMarker.getTitle())
                            .snippet("Lat: " + userMarker.getLocation().getLatitude() +
                                    " Lng: " + userMarker.getLocation().getLongitude()));
                    mapMarker.setTag(userMarker);
                    mMarkerHashMap.put(userMarker, mapMarker);
                }
//                // Index tracking approach
//                for (int index = 0; index >= newcomerMap.getMarkers().size(); index++) {
//                    UserMarker userMarker = newcomerMap.getMarkers().get(index);
//                    Marker mapMarker = mGoogleMap.addMarker(new MarkerOptions()
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
            mGoogleMap.setOnMapLongClickListener(onMapLongClickListener);
            // Set on MARKER TAP Listener
            mGoogleMap.setOnMarkerClickListener(onMarkerClickListener);
        }
    };

    private final AdapterView.OnItemClickListener onItemClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Marker mapMarker = mMarkerHashMap.get(newcomerMap.getMarkers().get(position));
            moveCamera(mapMarker.getPosition());
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
            Marker mapMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(
                            userMarker.getLocation().getLatitude(),
                            userMarker.getLocation().getLongitude()))
                    .title(userMarker.getTitle())
                    .snippet("Lat: " + userMarker.getLocation().getLatitude() +
                            " Lng: " + userMarker.getLocation().getLongitude()));
            mapMarker.setTag(userMarker);
            mMarkerHashMap.put(userMarker, mapMarker);
            mMarkerListAdapter.notifyDataSetChanged();
        }
    };

    private final GoogleMap.OnMarkerClickListener onMarkerClickListener
            = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            int markerIndex = newcomerMap.getMarkers().indexOf(marker.getTag());

            mListView.smoothScrollToPosition(markerIndex);
            return false;
        }
    };

}
