package brisk.bike.navigator.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Cosmic_M at 03.10.2017
 * Refactored by Cosmic_M at 24.8.2022
 */

public class MemoryPlaceBaseHelper extends SQLiteOpenHelper {
    public static final String NAME = "memory_place_base";
    public static final int VERSION = 1;

    public MemoryPlaceBaseHelper(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + SchemaDB.TABLE_NAME + "("
                + SchemaDB.Cols.ID + " integer primary key autoincrement, "
                + SchemaDB.Cols.LATITUDE + ", "
                + SchemaDB.Cols.LONGITUDE + ", "
                + SchemaDB.Cols.FILE_IMAGE_NAME + ", "
                + SchemaDB.Cols.DESCRIPTION +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }
}
