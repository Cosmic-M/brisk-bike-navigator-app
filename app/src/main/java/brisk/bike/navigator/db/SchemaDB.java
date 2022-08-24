package brisk.bike.navigator.db;

/**
 * Created by Cosmic_M at 03.10.2017
 * Refactored by Cosmic_M at 24.8.2022
 */

public class SchemaDB {
    public static final String TABLE_NAME = "memory_place_table";
    public static final class Cols{
        public static final String ID = "primary_key";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String FILE_IMAGE_NAME = "file_image_name";
        public static final String DESCRIPTION = "description";
    }
}
