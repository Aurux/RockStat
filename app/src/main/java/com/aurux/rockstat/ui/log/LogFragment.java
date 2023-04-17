package com.aurux.rockstat.ui.log;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.contract.ActivityResultContracts;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.aurux.rockstat.data.database.AppDatabase;
import com.aurux.rockstat.data.models.ClimbLogEntry;
import com.aurux.rockstat.databinding.FragmentLogBinding;


import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import android.location.Location;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.aurux.rockstat.R;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.libraries.places.api.Places;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;




import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


import android.util.Log;
import com.aurux.rockstat.data.database.AppDatabase;
import com.aurux.rockstat.data.dao.ClimbLogEntryDao;






public class LogFragment extends Fragment implements OnMapReadyCallback {

    private FragmentLogBinding binding;
    private GoogleMap mMap;
    private LocationCallback locationCallback;
    private TextView selectedPlaceTextView;


    private ActivityResultLauncher<String[]> requestPermissionsLauncher;

    private static final int SEARCH_RADIUS_METERS = 50;
    private static final String SEARCH_KEYWORD = "climb";

    private AppDatabase climbLogDatabase;

    private Handler debounceHandler = new Handler(Looper.getMainLooper());
    private Runnable searchPlacesRunnable;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        LogViewModel LogViewModel =
                new ViewModelProvider(this).get(LogViewModel.class);

        binding = FragmentLogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        selectedPlaceTextView = root.findViewById(R.id.selected_place_textview);

        // Set up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_location);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        String[] climbTypes = {"Bouldering", "Sport", "Trad"};
        String[] vGrades = {"VB", "V0", "V1", "V2", "V3", "V4", "V5", "V6", "V7", "V8", "V9", "V10", "V11", "V12", "V13", "V14", "V15", "V16", "V17"};
        String[] fGrades = {"1", "2", "3", "4a", "4b", "4c", "5a", "5b", "5c", "6a", "6b", "6c", "7a", "7b", "7c", "8a", "8b", "8c", "9a", "9b", "9c"};
        String[] tradGrades = {"Mod", "Diff", "VDiff", "HVD", "Sev", "HS", "VS", "HVS", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "E10", "E11"};

        Spinner typeSpinner = root.findViewById(R.id.spinner_climbing_type);
        Spinner gradeSpinner = root.findViewById(R.id.spinner_grade);

        // Set up the adapter for the type spinner
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, climbTypes);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);


        EditText nameOfRouteEditText = root.findViewById(R.id.et_route_name);
        // Set up a listener for the type spinner
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item from the type spinner
                String selectedType = (String) parent.getItemAtPosition(position);

                // Update the adapter of the grade spinner based on the selected item
                if (selectedType.equals("Bouldering")) {
                    ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, vGrades);
                    gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    gradeSpinner.setAdapter(gradeAdapter);

                    // Hide the name of route text field
                    nameOfRouteEditText.setVisibility(View.GONE);
                } else if (selectedType.equals("Sport")) {
                    ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, fGrades);
                    gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    gradeSpinner.setAdapter(gradeAdapter);
                    nameOfRouteEditText.setVisibility(View.VISIBLE);
                } else if (selectedType.equals("Trad")) {
                    ArrayAdapter<String> gradeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, tradGrades);
                    gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    gradeSpinner.setAdapter(gradeAdapter);
                    nameOfRouteEditText.setVisibility(View.VISIBLE);
                }
            }





            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });



        climbLogDatabase = AppDatabase.getInstance(getActivity());

        Button btnSubmit = root.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                // Gather data from input fields and create a LogEntry object
                ClimbLogEntry logEntry = new ClimbLogEntry();
                logEntry.setClimbingType(((Spinner) binding.spinnerClimbingType).getSelectedItem().toString());
                logEntry.setRouteName(((EditText) binding.etRouteName).getText().toString());
                logEntry.setGrade(((Spinner) binding.spinnerGrade).getSelectedItem().toString());
                logEntry.setTimestamp(System.currentTimeMillis());
                String attemptsStr = binding.etAttempts.getText().toString();
                if (attemptsStr.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter the number of attempts", Toast.LENGTH_SHORT).show();
                    return;
                }

                int attempts;

                try {
                    attempts = Integer.parseInt(attemptsStr);
                } catch (NumberFormatException e) {
                    attempts = 0;
                }

                logEntry.setAttempts(attempts);
                logEntry.setCompleted(binding.cbCompleted.isChecked());
                logEntry.setComment(((EditText) binding.etComment).getText().toString());
                float ratingFloat = binding.ratingBar.getRating();
                int rating = Math.round(ratingFloat);

                logEntry.setRating(rating);
                logEntry.setSelectedPlace(selectedPlaceTextView.getText().toString());

                // Insert the logEntry object into the database
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        long rowId = climbLogDatabase.climbLogEntryDao().insert(logEntry);
                        if (rowId == -1) {
                            Log.d("Database", "Insertion failed");
                        } else {
                            Log.d("Database", "Insertion successful, row ID: " + rowId);

                            // Clear input fields after successful submission
                            requireActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.etRouteName.setText("");
                                    binding.etAttempts.setText("");
                                    binding.cbCompleted.setChecked(false);
                                    binding.etComment.setText("");
                                    binding.ratingBar.setRating(0);
                                }
                            });
                        }
                    }
                }).start();

                // Show a message to inform the user that the data has been saved
                Toast.makeText(getActivity(), "Log entry saved", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        requestPermissionsLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean allPermissionsGranted = true;
            for (boolean permissionGranted : result.values()) {
                allPermissionsGranted &= permissionGranted;
            }

            if (allPermissionsGranted) {
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (mMap != null) {
                        setUpMap();
                    } else {
                        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_location);
                        if (mapFragment != null) {
                            mapFragment.getMapAsync(this);
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionsLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
        } else {
            setUpMap();
        }
    }

    // Set map current location if permissions are set.
    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        // Create a new instance of Google Places API client
        PlacesClient placesClient = Places.createClient(requireContext());

        mMap.setMyLocationEnabled(true);
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Update interval in milliseconds
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
                searchNearbyClimbingPlaces(currentLocation, placesClient, selectedPlaceTextView); // Add this line
            }
        });
        locationCallback = new LocationCallback() {

            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                Location location = locationResult.getLastLocation();
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        };

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

        mMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                mMap.clear(); // Clear any existing markers
                mMap.addMarker(new MarkerOptions().position(poi.latLng).title(poi.name));
                selectedPlaceTextView.setText(poi.name);
            }
        });

        mMap.setOnMapClickListener(latLng -> {
            mMap.clear(); // Clear any existing markers
            mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
            selectedPlaceTextView.setText(latLng.latitude + ", " + latLng.longitude);
        });


    }



    private void searchNearbyClimbingPlaces(LatLng currentLocation, PlacesClient placesClient, TextView selectedPlaceTextView) {
        double latitude = currentLocation.latitude;
        double longitude = currentLocation.longitude;

        // Calculate the search bounds
        double latDiff = SEARCH_RADIUS_METERS / 111000.0;
        double lonDiff = SEARCH_RADIUS_METERS / (111000.0 * Math.cos(Math.toRadians(latitude)));

        RectangularBounds searchBounds = RectangularBounds.newInstance(
                new LatLng(latitude - latDiff, longitude - lonDiff),
                new LatLng(latitude + latDiff, longitude + lonDiff)
        );

        // Create a new AutocompleteSessionToken
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        // Create a request to find places with the specified keyword
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationRestriction(searchBounds)
                .setSessionToken(token)
                .setQuery(SEARCH_KEYWORD)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build();

        // Send the request and handle the response
        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            if (!response.getAutocompletePredictions().isEmpty()) {
                AtomicInteger count = new AtomicInteger();
                for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                    String placeId = prediction.getPlaceId();

                    // Fetch the details of the place using its place ID
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
                    placesClient.fetchPlace(FetchPlaceRequest.newInstance(placeId, placeFields)).addOnSuccessListener((fetchPlaceResponse) -> {
                        Place place = fetchPlaceResponse.getPlace();
                        LatLng placeLatLng = place.getLatLng();
                        if (placeLatLng != null) {

                            mMap.addMarker(new MarkerOptions().position(placeLatLng).title(place.getName()));
                            if (count.getAndIncrement() == 0) {
                                selectedPlaceTextView.setText(place.getName());
                            }
                        }
                    });
                }
            } else {
                selectedPlaceTextView.setText(currentLocation.latitude + ", " + currentLocation.longitude);
                Toast.makeText(requireContext(), "No nearby climbing places found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMap != null) {
            if (locationCallback != null) {
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
                fusedLocationClient.removeLocationUpdates(locationCallback);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMap != null) {
            if (locationCallback != null) {
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
                LocationRequest locationRequest = LocationRequest.create();
                locationRequest.setInterval(10000); // Update interval in milliseconds
                locationRequest.setFastestInterval(5000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                try {
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                } catch (SecurityException e) {

                    Toast.makeText(requireContext(), "Location permission is required for this feature.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mMap != null) {
            mMap.clear();
            mMap = null;
        }
        if (binding != null) {
            binding = null;
        }
    }
}