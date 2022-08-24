package brisk.bike.navigator.modul;

import com.google.android.gms.maps.model.LatLng;
import java.util.List;

/**
 * Created by Cosmic_M at 03.10.2017
 * Refactored by Cosmic_M at 24.8.2022
 */

public class Route {
    public Distance distance;
    public Duration duration;
    public String startAddress;
    public LatLng startLocation;
    public String endAddress;
    public LatLng endLocation;
    public List<LatLng> points;
}
