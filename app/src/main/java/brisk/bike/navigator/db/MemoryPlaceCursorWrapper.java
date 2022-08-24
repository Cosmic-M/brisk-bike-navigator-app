package brisk.bike.navigator.db;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.google.android.gms.maps.model.LatLng;

import brisk.bike.navigator.modul.MemoryPlace;

/**
 * Created by Cosmic_M at 03.10.2017
 * Refactored by Cosmic_M at 24.8.2022
 */

public class MemoryPlaceCursorWrapper extends CursorWrapper {
    public MemoryPlaceCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public MemoryPlace getPlace(){
        int _id = getInt(getColumnIndex(SchemaDB.Cols.ID));
        double latitude = getDouble(getColumnIndex(SchemaDB.Cols.LATITUDE));
        double longitude = getDouble(getColumnIndex(SchemaDB.Cols.LONGITUDE));
        String file_name = getString(getColumnIndex(SchemaDB.Cols.FILE_IMAGE_NAME));
        String description = getString(getColumnIndex(SchemaDB.Cols.DESCRIPTION));
        LatLng latLng = new LatLng(latitude, longitude);
        return new MemoryPlace(_id, latLng, file_name, description);
    }
}
