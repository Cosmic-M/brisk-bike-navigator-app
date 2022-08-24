package brisk.bike.navigator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.google.android.gms.maps.model.LatLng;
import java.io.File;
import brisk.bike.navigator.modul.MemoryPlace;

/**
 * Created by Cosmic_M at 03.10.2017
 * Refactored by Cosmic_M at 24.8.2022
 */

public class AddPointActivity extends AppCompatActivity {
    private static final int REQUEST_PHOTO = 100;
    private static final String PHOTO_FILE = "photo_file";
    private ImageView mPhoto;
    private File mPhotoFile;
    private String et;
    private MemoryPlace mp;

    public static Intent newIntent(Context context){
        return new Intent(context, AddPointActivity.class);
    }

    @Override
    public void onSaveInstanceState(Bundle saveInstanceState){
        super.onSaveInstanceState(saveInstanceState);
        saveInstanceState.putSerializable(PHOTO_FILE, mPhotoFile);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point);
        TextView mLatitude = findViewById(R.id.tvLatitude);
        TextView mLongitude = findViewById(R.id.tvLongitude);
        Button mAddPoint = findViewById(R.id.btn_add_point);
        mPhoto = findViewById(R.id.image_id);
        EditText mNotationText = findViewById(R.id.et_notations);
        ImageButton mCameraButton = findViewById(R.id.camera_id);
        LatLng latLng = getIntent().getParcelableExtra("latlng");
        mLatitude.setText(String.valueOf(latLng.latitude));
        mLongitude.setText(String.valueOf(latLng.longitude));
        PackageManager packageManager = getApplicationContext().getPackageManager();
        mp = new MemoryPlace(latLng);
        if (savedInstanceState != null) {
            mPhotoFile = (File) savedInstanceState.getSerializable(PHOTO_FILE);
        } else {
            mPhotoFile = PlaceLab.get(getApplicationContext()).getPhotoFile(mp);
        }

        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && cameraIntent.resolveActivity(packageManager) != null;
        mPhoto.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", mPhotoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mCameraButton.setOnClickListener(v -> startActivityForResult(cameraIntent, REQUEST_PHOTO));
        mAddPoint.setOnClickListener(v -> {
            mp.setTextDescription(et);
            PlaceLab.get(getApplicationContext()).insertPlaceIntoDB(mp);
            setResult(RESULT_OK);
            finish();
        });

        mNotationText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                et = s.toString();
            }
        });

        updatePhotoView();
    }

    private void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
        }
        else{
            mPhoto.setVisibility(View.VISIBLE);
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), this);
            mPhoto.setMaxHeight(100);
            mPhoto.setMaxWidth(100);
            mPhoto.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_PHOTO){
            updatePhotoView();
        }
    }
}
