package tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;

public class YuuzuAlertDialog {

    private final Context context;

    public YuuzuAlertDialog(Context context) {
        this.context = context;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void showDialog(String title, String msg, AlertCallback alertCallback) {
        try {
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context);
            materialAlertDialogBuilder.setTitle(Html.fromHtml("<font color='#023246'>" + title + "</font>"));
            materialAlertDialogBuilder.setMessage(Html.fromHtml("<font color='#023246'>" + msg + "</font>"));
            materialAlertDialogBuilder.setBackground(context.getResources().getDrawable(R.drawable.alert_dialog, null));

            materialAlertDialogBuilder.setPositiveButton(R.string.okay, alertCallback::onOkay);

            materialAlertDialogBuilder.setNegativeButton(R.string.cancel, alertCallback::onCancel);

            materialAlertDialogBuilder.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface AlertCallback {
        void onOkay(DialogInterface dialog, int which);
        void onCancel(DialogInterface dialog, int which);
    }
}
