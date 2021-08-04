package tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon;

import java.util.HashMap;
import java.util.Map;

public class BeaconDefine {
    public static final String POINT_01 = "IPHONE 6S";
    public static final String POINT_02 = "IPHONE 12PRO";

    public Map<String, Map<String, String>> locations = new HashMap<>();

    public BeaconDefine() {
        initLocationData();
    }

    private void initLocationData() {
        Map<String, String> minorLocations = new HashMap<>();
        minorLocations.put("87", "Iphone 6s");
        minorLocations.put("89", "Iphone 12Pro");
        locations.put("94", minorLocations);
    }

    public String getLocationMsg(String major, String minor) {
        String location;
//        location = locations.get(major).get(minor);
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
