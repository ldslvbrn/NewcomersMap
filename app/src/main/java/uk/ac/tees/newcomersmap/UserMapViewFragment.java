package uk.ac.tees.newcomersmap;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

public class UserMapViewFragment extends Fragment {

    public static final String EXTRA_MAP_LIST_INDEX =
            "uk.ac.tees.newcomersmap.EXTRA_MAP_LIST_INDEX";

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final String TAG = "UserMapViewFragment";
    private static int DEFAULT_ZOOM_LEVEL = 13;

    // View variables
    private Toolbar mToolbar;
    private MapView mMapView;
    private UserMarkerListAdapter mListAdapter;
    private ListView mListView;

    // Utils variables
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private NewcomersMapViewModel mViewModel;
    private UserMap mUserMap;
    private WeakHashMap<UserMarker, Marker> mMarkerHashMap = new WeakHashMap<>();
    private boolean mapValueChanged;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        getActivity().addOnBackPressedCallback(onBackPressedCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_newcomer_map, container, false);
        if (savedInstanceState == null) {
            // Acquire ViewModel
            mViewModel = ViewModelProviders.of(getActivity())
                    .get(NewcomersMapViewModel.class);

            // Set-up ListView
            mListView = view.findViewById(R.id.listView_marker_list);

            // Set-up Toolbar
            mToolbar = view.findViewById(R.id.toolbar_newcomer_map);
            mToolbar.inflateMenu(R.menu.menu_context_map);
            mToolbar.setOnMenuItemClickListener(onToolbarMenuItemClickListener);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });

            // Get or create the UserMap object instance
            if (getArguments() != null) {
                mUserMap = mViewModel
                        .getAllMaps()
                        .getValue()
                        .get(getArguments().getInt(EXTRA_MAP_LIST_INDEX));
                mUserMap.setOnContentChangeListener(onContentChangeListener);
                mapValueChanged = false;
                mToolbar.setTitle(mUserMap.getTitle());
            } else {
                mUserMap = new UserMap();
                mUserMap.setMarkers(new ArrayList<UserMarker>());
                mUserMap.setTitle(getString(R.string.new_map));
                mapValueChanged = true;
                showSetMapTitleDialog(mUserMap);
                mToolbar.setTitle(mUserMap.getTitle());
            }

            // Set-up list view and adapter
            registerForContextMenu(mListView);
            mListAdapter = new UserMarkerListAdapter(getActivity(),
                    R.layout.item_user_marker, mUserMap.getMarkers());
            mListAdapter.setGeocoder(new Geocoder(getActivity()));
            mListView.setOnItemClickListener(onItemClickListener);
            mListView.setAdapter(mListAdapter);

            // Get MapView
            mMapView = view.findViewById(R.id.mapView);
            mMapView.onCreate(null);
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

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        switch (v.getId()) {
            case R.id.listView_marker_list:
                getActivity().getMenuInflater().inflate(R.menu.menu_floating_marker, menu);
                return;

            default:
                return;
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        switch (item.getItemId()) {
            case R.id.option_item_edit_marker_title:
                showSetMarkerTitleDialog(mUserMap.getMarkers().get(position));
                return true;

            case R.id.option_item_edit_description:
                showSetMarkerDescDialog(mUserMap.getMarkers().get(position));
                return true;

            case R.id.option_item_delete_marker:
                UserMarker userMarker = mUserMap.getMarkers().get(position);
                mMarkerHashMap.get(userMarker).remove();
                mMarkerHashMap.remove(userMarker);
                mUserMap.getMarkers().remove(userMarker);
                mapValueChanged = true;
                mUserMap.removeOnContentChangeListener(onContentChangeListener);
                mListAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onContextItemSelected(item);

        }
    }

    private void moveCamera(LatLng latLng) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                latLng, DEFAULT_ZOOM_LEVEL));
    }

    private void showSetMapTitleDialog(UserMap userMap) {
        MapTitleDialogFragment mapTitleDialogFragment = new MapTitleDialogFragment();
        mapTitleDialogFragment.setNewcomerMap(userMap);
        mapTitleDialogFragment.setDialogListener(new DialogListener() {
            @Override
            public void onDialogResult(DialogResult dialogResult) {
                if (dialogResult == DialogResult.INPUT_INVALID) {
                    Toast.makeText(getActivity(),
                            "The title must be in between 3-16 characters long", Toast.LENGTH_SHORT).show();
                } else mToolbar.setTitle(mUserMap.getTitle());
            }
        });
        mapTitleDialogFragment.showNow(getActivity().getSupportFragmentManager(),
                "Set Title Dialog");
    }

    private void showSetMarkerTitleDialog(final UserMarker marker) {
        MarkerTitleDialogFragment markerTitleDialogFragment = new MarkerTitleDialogFragment();
        markerTitleDialogFragment.setUserMarker(marker);
        markerTitleDialogFragment.setDialogListener(new DialogListener() {
            @Override
            public void onDialogResult(DialogResult dialogResult) {
                if (dialogResult == DialogResult.INPUT_INVALID) {
                    Toast.makeText(getActivity(),
                            "The title must be in between 3-16 characters long", Toast.LENGTH_SHORT).show();
                } else mMarkerHashMap.get(marker).setTitle(marker.getTitle());
            }
        });
        markerTitleDialogFragment.showNow(getActivity().getSupportFragmentManager(),
                "Set Title Dialog");
    }

    private void  showSetMarkerDescDialog(final UserMarker marker) {
        MarkerDescriptionDialogFragment markerDescriptionDialogFragment
                = new MarkerDescriptionDialogFragment();
        markerDescriptionDialogFragment.setUserMarker(marker);
        markerDescriptionDialogFragment.setDialogListener(new DialogListener() {
            @Override
            public void onDialogResult(DialogResult dialogResult) {
                if (dialogResult == DialogResult.INPUT_INVALID) {
                    Toast.makeText(getActivity(),
                            "The description must be up to 40 characters long", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (marker.getDescription() != null || !marker.getDescription().isEmpty()) {
                    mMarkerHashMap.get(marker).setSnippet(marker.getDescription());
                }
                // Refresh info window
                mMarkerHashMap.get(marker).showInfoWindow();
            }
        });
        markerDescriptionDialogFragment.showNow(getActivity().getSupportFragmentManager(),
                "Set Description Dialog");
    }

    private void returnToMapList() {
        getActivity().removeOnBackPressedCallback(onBackPressedCallback);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                .navigate(R.id.action_newcomerMapFragment_to_mapListFragment);
    }

    private void saveNewcomerMap(NewcomersMapViewModel.OnServiceResultListener onServiceResultListener) {
        // Validation checks
        if (mUserMap.getMarkers().isEmpty()) {
            Toast.makeText(getActivity(),
                    "At least one marker placed is required.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        // Save data
        if (getArguments() != null) {
            mViewModel.updateUserMap(mUserMap, onServiceResultListener);
        } else {
            // Name must be unique
            for (UserMap map : mViewModel.getAllMaps().getValue()) {
                if (mUserMap.getTitle().equals(map.getTitle())) {
                    Toast.makeText(getActivity(),
                            "A map with this name already exists.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            mUserMap.setLocation(mUserMap.getMarkers().get(0).getLocation());
            mViewModel.addUserMap(mUserMap, onServiceResultListener);
        }
    }

    private void deleteNewcomerMap(NewcomersMapViewModel.OnServiceResultListener onServiceResultListener) {
        mViewModel.deleteUserMap(mUserMap, onServiceResultListener);
    }

    private final OnBackPressedCallback onBackPressedCallback
            = new OnBackPressedCallback() {
        @Override
        public boolean handleOnBackPressed() {
            if (mapValueChanged) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.unsaved_changes_message)
                        .setTitle(R.string.unsaved_changes_title);
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        returnToMapList();
                    }
                });
                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveNewcomerMap(new NewcomersMapViewModel.OnServiceResultListener() {
                            @Override
                            public void OnResultCallback(Boolean isSuccessful) {
                                if (isSuccessful) {
                                    returnToMapList();
                                    Toast.makeText(getActivity(), R.string.save_success, Toast.LENGTH_SHORT).show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle(R.string.save_map);
                                    builder.setPositiveButton("OK",null);
                                    builder.setMessage(R.string.save_error);
                                    builder.setIcon(R.drawable.ic_warning);
                                    builder.show();
                                }
                            }
                        });
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            } else {
                return false;
            }

        }
    };

    private final Toolbar.OnMenuItemClickListener onToolbarMenuItemClickListener
            = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.option_item_save_map:
                    if (mapValueChanged) {
                        saveNewcomerMap(new NewcomersMapViewModel.OnServiceResultListener() {
                            @Override
                            public void OnResultCallback(Boolean isSuccessful) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(R.string.save_map);
                                builder.setPositiveButton("OK",null);
                                if (isSuccessful) {
                                    builder.setMessage(R.string.save_success);
                                    builder.setIcon(R.drawable.ic_check);
                                    mapValueChanged = false;
                                    mUserMap.setOnContentChangeListener(onContentChangeListener);
                                } else {
                                    builder.setMessage(R.string.save_error);
                                    builder.setIcon(R.drawable.ic_warning);
                                }
                                builder.show();
                            }
                        });
                    }
                    return true;

                case R.id.option_item_edit_map_title:
                    showSetMapTitleDialog(mUserMap);
                    return true;

                case R.id.option_item_delete_map:
                    deleteNewcomerMap(new NewcomersMapViewModel.OnServiceResultListener() {
                        @Override
                        public void OnResultCallback(Boolean isSuccessful) {
                            if (isSuccessful) {
                                returnToMapList();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle(R.string.save_map);
                                builder.setPositiveButton("OK",null);
                                builder.setMessage(R.string.save_error);
                                builder.setIcon(R.drawable.ic_warning);
                                builder.show();
                            }
                        }
                    });
                    return true;

                case R.id.option_item_publish_map:
                    Toast.makeText(getActivity(), getString(R.string.coming_soon), Toast.LENGTH_SHORT).show();
                    return true;

                default:
                    return false;
            }
        }
    };

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
                Bundle bundle = new Bundle();
                bundle.putInt(ErrorFragment.EXTRA_ERROR_CODE, ErrorFragment.PERMISSION_ERROR_CODE);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment)
                        .navigate(R.id.action_newcomerMapFragment_to_errorFragment, bundle);
            } else {
                // Permissions OK:
                // Show and animate camera to the current location
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                // Get current device location ASYNCHRONOUSLY
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                final Task locationTask = mFusedLocationClient.getLastLocation();
                locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            if (getArguments() != null) {
                                GeoPoint geoPoint = mUserMap.getLocation();
                                moveCamera(new LatLng(
                                        geoPoint.getLatitude(),
                                        geoPoint.getLongitude()));
                            } else {
                                Location location = task.getResult();
                                moveCamera(new LatLng(
                                        location.getLatitude(),
                                        location.getLongitude()
                                ));
                            }
                        }
                    }
                });
                // Populate map with markers, set display info and store their reference
                for (UserMarker userMarker : mUserMap.getMarkers()) {
                    Marker mapMarker = mGoogleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(
                                    userMarker.getLocation().getLatitude(),
                                    userMarker.getLocation().getLongitude()))
                            .title(userMarker.getTitle()));
                    if (userMarker.getDescription() != null && !userMarker.getDescription().isEmpty()) {
                        mapMarker.setSnippet(userMarker.getDescription());
                    }
                    mapMarker.setTag(userMarker);
                    userMarker.setOnContentChangeListener(onContentChangeListener);
                    mMarkerHashMap.put(userMarker, mapMarker);
                }
                mListAdapter.notifyDataSetChanged();
                // Set on MAP HOLD Listener
                mGoogleMap.setOnMapLongClickListener(onMapLongClickListener);
                // Set on MARKER TAP Listener
                mGoogleMap.setOnMarkerClickListener(onMarkerClickListener);
            }
        }
    };

    private final AdapterView.OnItemClickListener onItemClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Marker mapMarker = mMarkerHashMap.get(mUserMap.getMarkers().get(position));
            moveCamera(mapMarker.getPosition());
            mapMarker.showInfoWindow();
        }
    };

    private final GoogleMap.OnMapLongClickListener onMapLongClickListener
            = new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            UserMarker userMarker = new UserMarker();
            userMarker.setTitle("New Marker");
            showSetMarkerTitleDialog(userMarker);
            userMarker.setLocation(new GeoPoint(latLng.latitude, latLng.longitude));
            mUserMap.getMarkers().add(userMarker);
            Marker mapMarker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(
                            userMarker.getLocation().getLatitude(),
                            userMarker.getLocation().getLongitude()))
                    .title(userMarker.getTitle()));
            if (userMarker.getDescription() != null && !userMarker.getDescription().isEmpty()) {
                mapMarker.setSnippet(userMarker.getDescription());
            }
            mapMarker.setTag(userMarker);
            mMarkerHashMap.put(userMarker, mapMarker);
            mListAdapter.notifyDataSetChanged();
        }
    };

    private final GoogleMap.OnMarkerClickListener onMarkerClickListener
            = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
            int markerIndex = mUserMap.getMarkers().indexOf(marker.getTag());
            mListView.smoothScrollToPosition(markerIndex);
            marker.showInfoWindow();
            return true;
        }
    };

    private final UserMap.OnContentChangeListener onContentChangeListener
            = new UserMap.OnContentChangeListener() {
        @Override
        public void onContentChange(boolean b) {
            mapValueChanged = b;
            if (b == true) {
                mUserMap.removeOnContentChangeListener(this);
            }
        }
    };
}
