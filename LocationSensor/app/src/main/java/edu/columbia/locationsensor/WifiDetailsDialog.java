package edu.columbia.locationsensor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Pooja on 4/9/15.
 */
public class WifiDetailsDialog extends DialogFragment {
    WifiNetwork wifiNetwork;

    public WifiDetailsDialog(){}

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        Bundle args = getArguments();
        if(args != null) {
            wifiNetwork = (WifiNetwork)args.getSerializable("WifiNetwork");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(wifiNetwork.toString());
        builder.setTitle(getString(R.string.wifi_dialog_title) + " " + wifiNetwork.getSSID());
        builder.setIcon(R.drawable.ic_wifi_small);
        builder.setNeutralButton(R.string.mesg_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
