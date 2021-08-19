package tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

public class PermissionRequest {

    public PermissionRequest() {

    }

    public void requestBluetooth(Context context) {
        BluetoothAdapter mBluetoothAdapter;

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(context, "您的手機無法使用該應用...", Toast.LENGTH_SHORT).show();

        }

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "您的手機無法使用該應用...", Toast.LENGTH_SHORT).show();

        }

        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            context.startActivity(enableBluetooth);
        }
    }
}
