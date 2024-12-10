package fr.uge.structsure.bluetooth.cs108;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import fr.uge.structsure.MainActivity;

import com.csl.cslibrary4a.ReaderDevice;

import java.util.ArrayList;

public class Connexion {
    private DeviceScanTask deviceScanTask;
    private DeviceConnectTask deviceConnectTask;
    public final ArrayList<ReaderDevice> readersList = new ArrayList<>();
    private final Handler mHandler = new Handler();
    private Context context = null;

    public Connexion(Context context) {
        this.context = context;
        if (!MainActivity.csLibrary4A.isBleConnected()) readersList.clear();
        checkRunnable.run();
    }

    public void onItemClick(ReaderDevice readerDevice) {
//        ReaderDevice readerDevice = readersList.get(position);    // Un ReaderDevice est l'objet qui représente un périphérique BLE compatible

        if (MainActivity.csLibrary4A.isBleConnected() && readerDevice.isConnected() && (readerDevice.getSelected())) {
            // If THIS device is already connected, disconnect the device
            System.out.println("[CONNECT] - Disconnecting from device " + readerDevice.getName());
            MainActivity.csLibrary4A.disconnect(false);
            readersList.clear();
        } else if (!MainActivity.csLibrary4A.isBleConnected() && !readerDevice.getSelected()) {
            // If not connected yet to the BLE device
            boolean validStart = deviceConnectTask == null || deviceConnectTask.getStatus() == AsyncTask.Status.FINISHED;
            if (validStart) {    // Creates a new DeviceConnectTask if not started yet
                if (deviceScanTask != null) deviceScanTask.cancel(true);    // Stops the scan task?
                System.out.println("[CONNECT] - Connecting to device " + readerDevice.getName());
                deviceConnectTask = new DeviceConnectTask(readerDevice);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    deviceConnectTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    deviceConnectTask.execute();
                }
            }
        }

//                if (readersList.size() > position) {    // Update displayed list
//                  readerDevice.setSelected(!readerDevice.getSelected());    // Invert the selected state of the device
//                    readersList.set(position, readerDevice);
//                    for (int i = 0; i < readersList.size(); i++) {
//                        if (i != position) {
//                            ReaderDevice readerDevice1 = readersList.get(i);
//                            if (readerDevice1.getSelected()) {
//                                readerDevice1.setSelected(false);
//                                readersList.set(i, readerDevice1);
//                            }
//                        }
//                    }
//                }
//                readerListAdapter.notifyDataSetChanged();
    }

    /**
     * Once connected
     */
    public void onStop() {
        mHandler.removeCallbacks(checkRunnable);
        if (deviceScanTask != null) {
            deviceScanTask.cancel(true);
        }
        if (deviceConnectTask != null) {
            deviceConnectTask.cancel(true);
        }
    }

    private final Runnable checkRunnable = new Runnable() {
        @Override
        public void run() {
            boolean operating = MainActivity.csLibrary4A.isBleConnected();
            if (!operating && deviceScanTask != null) {
                if (!deviceScanTask.isCancelled())   operating = true;
            }
            if (!operating && deviceConnectTask != null) {
                if (!deviceConnectTask.isCancelled())   operating = true;
            }
            if (!operating) {
                deviceScanTask = new DeviceScanTask(context, readersList, r -> onItemClick(r));
                deviceScanTask.execute();
            }
            mHandler.postDelayed(checkRunnable, 5000);
        }
    };
}
