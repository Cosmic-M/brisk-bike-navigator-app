package brisk.bike.navigator;

import static com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import brisk.bike.navigator.modul.DirectionFinder;
import brisk.bike.navigator.modul.DirectionFinderListener;
import brisk.bike.navigator.modul.MemoryPlace;
import brisk.bike.navigator.modul.Route;

/**
 * Created by Cosmic_M at 03.10.2017
 * Refactored by Cosmic_M at 24.8.2022
 */

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, DirectionFinderListener, RouteComposeListener, OnMarkerClickListener {
    private static final int PERMISSION_FOR_LOCATION_CODE = 250;
    private static final String DESTINATION = "destination";
    private static final String LOCATION = "location";
    private static final String LATITUDE_LONGITUDE = "lat_Lng";
    private static final String ROW_MUST_DELETED = "rowMustDelete";
    private static final String POLYLINE = "polyline";
    private static final String DISTANCE = "distance";
    private static final String TAG = "tag";
    private static final String FLAG = "flag";
    private static final String LIST_SAVED_LOCATION = "listSavedLocation";
    private static final int REQUEST_NEW_POINT = 250;
    private static final int REQUEST_NEW_POINT_REMOVE_OLD_DESTINATION = 350;
    private static final int PREVIEW_POINTS = 450;
    private GoogleMap mMap;
    private List<MemoryPlace> mListSavedLocations;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private LocationManager locationManager;
    private LatLng myLocation;
    private int idRowMustDelete;
    private LatLng mDestinationPoint;
    private List<LatLng> mTransitionPoints = new ArrayList<>();
    private PolylineOptions mPolylineOptions;
    private String mDistance;
    private int mTag = -1;
    private boolean flag;

    @Override
    public void onSaveInstanceState(@NonNull Bundle saveInstanceState) {
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putParcelable(DESTINATION, mDestinationPoint);
        saveInstanceState.putParcelable(LOCATION, myLocation);
        saveInstanceState.putParcelableArrayList(LATITUDE_LONGITUDE, (ArrayList<? extends Parcelable>) mTransitionPoints);
        saveInstanceState.putInt(ROW_MUST_DELETED, idRowMustDelete);
        saveInstanceState.putParcelable(POLYLINE, mPolylineOptions);
        saveInstanceState.putString(DISTANCE, mDistance);
        saveInstanceState.putInt(TAG, mTag);
        saveInstanceState.putBoolean(FLAG, flag);
        saveInstanceState.putParcelableArrayList(LIST_SAVED_LOCATION, (ArrayList<? extends Parcelable>) mListSavedLocations);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState != null) {
            mDestinationPoint = savedInstanceState.getParcelable(DESTINATION);
            myLocation = savedInstanceState.getParcelable(LOCATION);
            mTransitionPoints = savedInstanceState.getParcelableArrayList(LATITUDE_LONGITUDE);
            idRowMustDelete = savedInstanceState.getInt(ROW_MUST_DELETED);
            mPolylineOptions = savedInstanceState.getParcelable(POLYLINE);
            mDistance = savedInstanceState.getString(DISTANCE);
            mTag = savedInstanceState.getInt(TAG);
            flag = savedInstanceState.getBoolean(FLAG);
            mListSavedLocations = savedInstanceState.getParcelableArrayList(LIST_SAVED_LOCATION);
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Button mFindPath = findViewById(R.id.btnFindPath);
        Button mShowAllPlaces = findViewById(R.id.showAllPlaces);
        mFindPath.setOnClickListener(v -> {
            if (!isNetworkAvailableAndConnected()) {
                Toast.makeText(MapsActivity.this, "no internet access", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mDestinationPoint == null) {
                Toast.makeText(MapsActivity.this, "Destination Is Absent!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mTag != -1) {
                onRemoveFragment(mTag);
                mTag = -1;
            }
            sendRequest();
        });
        mShowAllPlaces.setOnClickListener(v -> updateCamera(true));
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        return isNetworkAvailable && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    PERMISSION_FOR_LOCATION_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20000, 10, mLocationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 20000, 10, mLocationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        final Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.clear_all_appointed_points_item:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
                alertDialog.setTitle(R.string.request);
                alertDialog.setPositiveButton(R.string.yes, (dialog, which) -> {
                    mPolylineOptions = null;
                    mDestinationPoint = null;
                    mTransitionPoints.clear();
                    mDistance = "0 km";
                    ((TextView) findViewById(R.id.tvDistance)).setText(mDistance);
                    if (mTag != -1) {
                        onRemoveFragment(mTag);
                        mTag = -1;
                    }
                    updateCamera(true);
                    Toast.makeText(MapsActivity.this, "CLEAR", Toast.LENGTH_SHORT).show();
                });
                alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                    }
                });
                AlertDialog dialog = alertDialog.create();
                dialog.show();
                return true;
            case R.id.preview_points_item:
                intent = PlacePagerActivity.newIntent(this);
                startActivityForResult(intent, PREVIEW_POINTS);
                onRemoveFragment(mTag);
                return true;
            case R.id.new_point_item:
                onRemoveFragment(mTag);
                if (myLocation == null) {
                    Toast.makeText(this, "try later, current location is't detected", Toast.LENGTH_SHORT).show();
                    return true;
                }
                String lat = String.format("%.8g%n", myLocation.latitude).replace(",", ".");
                String lon = String.format("%.8g%n", myLocation.longitude).replace(",", ".");
                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lon);
                LatLng trimLatLng = new LatLng(latitude, longitude);
                MemoryPlace alongside = isNearSavedLocation(myLocation);
                intent = AddPointActivity.newIntent(MapsActivity.this);
                intent.putExtra("latlng", trimLatLng);
                if (alongside == null) {
                    startActivityForResult(intent, REQUEST_NEW_POINT);
                } else if (isNearSavedLocation(myLocation) != null) {
                    idRowMustDelete = alongside.getIdRowDb();
                    LatLng near = alongside.getLatLng();
                    String stringSubtitle = getString(R.string.add_point_question_dialog, "point lat:"
                            + new Formatter().format("%.6f", near.latitude)
                            + ", lng:" + new Formatter().format("%.6f", near.longitude));
                    ChoiceDialog choiceDialog = ChoiceDialog.newInstance(stringSubtitle, myLocation);
                    choiceDialog.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                    choiceDialog.show(getSupportFragmentManager(), "choice_dialog");
                }
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_NEW_POINT) {
            updateCamera(true);
        } else if (requestCode == REQUEST_NEW_POINT_REMOVE_OLD_DESTINATION) {
            PlaceLab.get(this).removeRowDbById(idRowMustDelete);
            updateCamera(true);
        } else if (requestCode == PREVIEW_POINTS) {
            updateCamera(true);
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
            if (!flag) {
                Toast.makeText(MapsActivity.this, "YOUR LOCATION IS APPOINTED", Toast.LENGTH_SHORT).show();
                flag = true;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private MemoryPlace isNearSavedLocation(@NonNull LatLng currentLocation) {
        for (int i = 0; i < mListSavedLocations.size(); i++) {
            LatLng latLng = mListSavedLocations.get(i).getLatLng();
            if ((currentLocation.latitude < (latLng.latitude + 0.01)
                    && currentLocation.latitude > (latLng.latitude - 0.01))
                    && (currentLocation.longitude < (latLng.longitude + 0.01)
                    && currentLocation.longitude > (latLng.longitude - 0.01))) {
                return mListSavedLocations.get(i);
            }
        }
        return null;
    }

    private void sendRequest() {
        if (myLocation == null || mDestinationPoint == null) {
            Toast.makeText(this, "current location or destination point is't determined", Toast.LENGTH_SHORT).show();
            return;
        }
        mMap.clear();
        String origin = myLocation.latitude + ", " + myLocation.longitude;
        String destination = mDestinationPoint.latitude + ", " + mDestinationPoint.longitude;
        try {
            try {
                new DirectionFinder(this, origin, destination, convertLatLngListToStringList(mTransitionPoints)).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedOperationException exc) {
            exc.printStackTrace();
        }
    }

    private static List<String> convertLatLngListToStringList(List<LatLng> latLngsList) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < latLngsList.size(); i++) {
            latLngsList.size();
            list.add(latLngsList.get(i).latitude +
                    "%2C" + latLngsList.get(i).longitude + "%7C");
        }
        return list;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        updateCamera(true);
    }

    @Override
    public void onDetailedPointShow(int tag) {
        MemoryPlace mp = PlaceLab.get(this).getMemoryPlace().get(tag);
        Intent intent = DetailPlaceActivity.newInstance(getApplicationContext(), mp);
        startActivity(intent);
    }

    @Override
    public int onAssignTransitionPoint(int tag) {
        int resource;
        MemoryPlace mp = PlaceLab.get(this).getMemoryPlace().get(tag);
        if (!mTransitionPoints.contains(mp.getLatLng())) {
            resource = R.mipmap.transit_cancel;
            mTransitionPoints.add(mp.getLatLng());
            if (mp.getLatLng().equals(mDestinationPoint)) {
                mDestinationPoint = null;
            }
        } else {
            resource = R.mipmap.transition_flag;
            mTransitionPoints.remove(mp.getLatLng());
        }
        updateCamera(false);
        return resource;
    }

    @Override
    public int onAssignDestinationPoint(int tag) {
        int resource = 0;
        MemoryPlace mp = PlaceLab.get(this).getMemoryPlace().get(tag);
        if (mTransitionPoints.contains(mp.getLatLng())) {
            resource = R.mipmap.transition_flag;
        }
        mTransitionPoints.remove(mp.getLatLng());
        mDestinationPoint = mp.getLatLng();
        updateCamera(false);
        return resource;
    }

    @Override
    public void onRemoveFragment(int tag) {
        if (getSupportFragmentManager().findFragmentByTag("fragment") != null) {
            getSupportFragmentManager().beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentByTag("fragment"))
                    .commit();
        }
    }

    @Override
    public int getResourceForTransitionImage(int tag) {
        MemoryPlace mp = PlaceLab.get(this).getMemoryPlace().get(tag);
        if (mTransitionPoints.contains(mp.getLatLng())) {
            return R.mipmap.transit_cancel;
        } else {
            return R.mipmap.transition_flag;
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull final Marker marker) {
        int tag;
        if (marker.getTag() != null) {
            tag = (int) marker.getTag();
            mTag = tag;
            TinyPictureFragment fragment = TinyPictureFragment.newInstance(tag);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container_id, fragment, "fragment")
                    .commit();
        }
        return false;
    }

    private void updateCamera(boolean withFocusing) {
        if (mMap == null) {
            return;
        }
        if (mPolylineOptions != null) {
            mMap.clear();
            polylinePaths.add(mMap.addPolyline(mPolylineOptions));
            LatLngBounds.Builder bounds = new LatLngBounds.Builder();

            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.start_marker))
                    .position(myLocation));
            bounds.include(myLocation);

            for (LatLng point : mTransitionPoints) {
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.transition_marker))
                        .position(point));
                bounds.include(point);
            }
            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.finish_marker))
                    .position(mDestinationPoint));
            bounds.include(mDestinationPoint);

            ((TextView) findViewById(R.id.tvDistance)).setText(mDistance);

            int margin = getResources().getDimensionPixelSize(R.dimen.map_insert_margin);
            final CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds.build(), margin);
            if (androidx.core.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && androidx.core.app.ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_FOR_LOCATION_CODE);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        PERMISSION_FOR_LOCATION_CODE);
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.animateCamera(update);
            return;
        }
        originMarkers.clear();
        mMap.clear();
        mListSavedLocations = PlaceLab.get(getApplicationContext()).getMemoryPlace();
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (int i = 0; i < mListSavedLocations.size(); i++) {
            bounds.include(mListSavedLocations.get(i).getLatLng());
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title("point " + i)
                    .position(mListSavedLocations.get(i).getLatLng())));
            originMarkers.get(i).setTag(i);
            if (mListSavedLocations.get(i).getLatLng().equals(mDestinationPoint)) {
                Marker marker = originMarkers.get(i);
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            }
            if (mTransitionPoints.contains(mListSavedLocations.get(i).getLatLng())) {
                Marker marker = originMarkers.get(i);
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            }
        }
        if (myLocation != null) {
            bounds.include(myLocation);
        }
        if (withFocusing) {
            int margin = getResources().getDimensionPixelSize(R.dimen.map_insert_margin);

            final CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds.build(), margin);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                    .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission
                    .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapLoadedCallback(() -> mMap.animateCamera(update));
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }
        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }
        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();
        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            mDistance = route.distance.text;
            TextView viewById = findViewById(R.id.tvDistance);
            viewById.setBackgroundColor(Color.parseColor("#000000"));
            viewById.setText(mDistance);
            bounds.include(myLocation);
            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.start_marker))
                    .title(route.startAddress)
                    .position(route.startLocation)));
            for (LatLng point : mTransitionPoints) {
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.transition_marker))
                        .title("transit point")
                        .position(point));
                bounds.include(point);
            }
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.finish_marker))
                    .title(route.endAddress)
                    .position(route.endLocation)));
            bounds.include(mDestinationPoint);
            mPolylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.RED).
                    width(10);
            for (int i = 0; i < route.points.size(); i++)
                mPolylineOptions.add(route.points.get(i));
            polylinePaths.add(mMap.addPolyline(mPolylineOptions));
            int margin = getResources().getDimensionPixelSize(R.dimen.map_insert_margin);
            final CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds.build(), margin);
            mMap.animateCamera(update);
        }
    }
}
