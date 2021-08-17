package tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon;

import java.util.HashMap;
import java.util.Map;

public class BeaconDefine {
    public static final String POINT_01 = "IPHONE 6S";
    public static final String POINT_02 = "IPHONE 12 PRO";
    public static final String POINT_03 = "IPHONE 12 PRO MAX";

    public static final String IBEACON_10 = "IBEACON_10"; //Major 122, Minor 1221
    public static final String IBEACON_11 = "IBEACON_11"; //Major 123, Minor 1231
    public static final String IBEACON_21 = "IBEACON_21"; //Major 1  , Minor 11365

    public Map<String, Map<String, String>> locations = new HashMap<>();

    public BeaconDefine() {
        initLocationData();
    }

    private void initLocationData() {
        Map<String, String> iphoneLocation = new HashMap<>();
        Map<String, String> ibeacon_10_Location = new HashMap<>();
        Map<String, String> ibeacon_11_Location = new HashMap<>();
        Map<String, String> ibeacon_21_Location = new HashMap<>();

        // Minor
        iphoneLocation.put("87", POINT_01);
        iphoneLocation.put("89", POINT_02);
        iphoneLocation.put("99", POINT_03);

        // Minor
        ibeacon_10_Location.put("1221", IBEACON_10);
        ibeacon_11_Location.put("1231", IBEACON_11);
        ibeacon_21_Location.put("11365", IBEACON_21);

        // Major
        locations.put("94", iphoneLocation);
        locations.put("122", ibeacon_10_Location);
        locations.put("123", ibeacon_11_Location);
        locations.put("1", ibeacon_21_Location);
    }

    public String getLocationMsg(String major, String minor) {
        String location;

        Map<String, String> minorMap = locations.get(major);
        if (minorMap == null || minorMap.size() == 0) {
            return "暂无位置信息";
        }
        location = minorMap.get(minor);
        if (location == null || location.equals("")) {
            return "暂无位置信息";
        }
        return location;
    }
}
