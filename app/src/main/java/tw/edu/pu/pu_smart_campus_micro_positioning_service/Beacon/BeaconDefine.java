package tw.edu.pu.pu_smart_campus_micro_positioning_service.Beacon;

import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.HashMap;
import java.util.Map;

public class BeaconDefine {
    public static final Region REGION_BEACON_01 =
            new Region("REGION_BEACON_01", Identifier.parse("699ebc80-e1f3-11e3-9a0f-0cf3ee3bc012"), null, null);

    public static final String IBEACON_10 = "主顧聖母堂"; //Major 122, Minor 1221
    public static final String IBEACON_11 = "羅馬競技場"; //Major 123, Minor 1231
    public static final String IBEACON_21 = "若望保祿二世體育館"; //Major 1  , Minor 11365

    public Map<String, Map<String, String>> locations = new HashMap<>();

    public BeaconDefine() {
        initLocationData();
    }

    private void initLocationData() {
        Map<String, String> ibeacon_10_Location = new HashMap<>();
        Map<String, String> ibeacon_11_Location = new HashMap<>();
        Map<String, String> ibeacon_21_Location = new HashMap<>();

        // Minor
        ibeacon_10_Location.put("1221", IBEACON_10);
        ibeacon_11_Location.put("1231", IBEACON_11);
        ibeacon_21_Location.put("11365", IBEACON_21);

        // Major
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
