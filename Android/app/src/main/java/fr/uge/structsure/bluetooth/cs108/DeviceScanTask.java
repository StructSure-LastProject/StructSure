package fr.uge.structsure.bluetooth.cs108;


import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import fr.uge.structsure.MainActivity;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.csl.cslibrary4a.ReaderDevice;
import com.csl.cslibrary4a.BluetoothGatt;

class DeviceScanTask extends AsyncTask<Void, String, String> {
    private long timeMillisUpdate = System.currentTimeMillis();
    ArrayList<ReaderDevice> readersListOld = new ArrayList<ReaderDevice>();
    boolean wait4process = false; boolean scanning = false, DEBUG = false;
    private final Context context;

    private final ArrayList<BluetoothGatt.Cs108ScanData> mScanResultList = new ArrayList<>();
    private final ArrayList<ReaderDevice> readersList;
    private final Consumer<ReaderDevice> callback;

    public DeviceScanTask(Context content, ArrayList<ReaderDevice> readersList, Consumer<ReaderDevice> callback) {
        this.context = content;
        this.readersList = readersList;
        this.callback = callback;
    }

    @Override
    protected String doInBackground(Void... a) {
        while (!isCancelled()) {
            if (!wait4process) {
                BluetoothGatt.Cs108ScanData cs108ScanData = MainActivity.csLibrary4A.getNewDeviceScanned();
                if (cs108ScanData != null) mScanResultList.add(cs108ScanData);
                if (!scanning || mScanResultList.size() != 0 || System.currentTimeMillis() - timeMillisUpdate > 10000) {
                    wait4process = true; publishProgress("");
                }
            }
        }
        return "End of Asynctask()";
    }

    @Override
    protected void onProgressUpdate(String... output) {
        if (!scanning) {    // Starts the scan the first time
            scanning = true;
            if (!MainActivity.csLibrary4A.scanLeDevice(true)) cancel(true);
        }
        boolean listUpdated = false;
        while (mScanResultList.size() != 0) {
            BluetoothGatt.Cs108ScanData scanResultA = mScanResultList.remove(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {     // Ask for BLE authorizations?
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) continue;
            } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) continue;
            if (scanResultA.device.getType() == BluetoothDevice.DEVICE_TYPE_LE) {
                boolean match = false;
                for (int i = 0; i < readersList.size(); i++) {    // Increment the match counter if already seen
                    if (readersList.get(i).getAddress().matches(scanResultA.device.getAddress())) {
                        ReaderDevice readerDevice1 = readersList.get(i);
                        int count = readerDevice1.getCount();
                        count++;
                        readerDevice1.setCount(count);
                        readerDevice1.setRssi(scanResultA.rssi);
                        readersList.set(i, readerDevice1); listUpdated = true;
                        match = true;
                        break;
                    }
                }
                if (!match) {    // First time detected
                    System.out.println("NEW DEVICE DETECTED: name=" + scanResultA.device.getName() + ", address=" + scanResultA.device.getAddress());
                    ReaderDevice readerDevice = new ReaderDevice(scanResultA.device.getName(), scanResultA.device.getAddress(), false, "", 1, scanResultA.rssi, scanResultA.serviceUUID2p2);
                    String strInfo = "";
                    if (scanResultA.device.getBondState() == 12) {
                        strInfo += "BOND_BONDED\n";
                    }
                    readerDevice.setDetails(strInfo + "scanRecord=" + MainActivity.csLibrary4A.byteArrayToString(scanResultA.scanRecord));
                    readersList.add(readerDevice); listUpdated = true;
                    callback.accept(readerDevice); // FIXME temporary way to connect to the device
                }
            } else {
                if (DEBUG) MainActivity.csLibrary4A.appendToLog("deviceScanTask: rssi=" + scanResultA.rssi + ", error type=" + scanResultA.device.getType());
            }
        }
        if (System.currentTimeMillis() - timeMillisUpdate > 10000) {  // TODO Inspect, strange things seems to happen here
            timeMillisUpdate = System.currentTimeMillis();
            for (int i = 0; i < readersList.size(); i++) {
                ReaderDevice readerDeviceNew = readersList.get(i);
                boolean matched = false;
                for (int k = 0; k < readersListOld.size(); k++) {
                    ReaderDevice readerDeviceOld = readersListOld.get(k);
                    if (readerDeviceOld.getAddress().matches(readerDeviceNew.getAddress())) {
                        matched = true;
                        if (readerDeviceOld.getCount() >= readerDeviceNew.getCount()) {
                            readersList.remove(i);
                            listUpdated = true;
                            readersListOld.remove(k);
                        } else readerDeviceOld.setCount(readerDeviceNew.getCount());
                        break;
                    }
                }
                if (!matched) {
                    ReaderDevice readerDevice1 = new ReaderDevice(null, readerDeviceNew.getAddress(), false, null, readerDeviceNew.getCount(), 0);
                    readersListOld.add(readerDevice1);
                }
            }
            if (DEBUG) MainActivity.csLibrary4A.appendToLog("Matched. Updated readerListOld with size = " + readersListOld.size());
            MainActivity.csLibrary4A.scanLeDevice(false);
            scanning = false;
        }
        if (listUpdated) {
            // Updates the displayed devices
        }
        wait4process = false;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (DEBUG) MainActivity.csLibrary4A.appendToLog("Stop Scanning 1A");
        deviceScanEnding();
    }

    @Override
    protected void onPostExecute(String result) {
        if (DEBUG) MainActivity.csLibrary4A.appendToLog("Stop Scanning 1B");
        deviceScanEnding();
    }

    /**
     * Stops the devices scan
     */
    void deviceScanEnding() {
        MainActivity.csLibrary4A.scanLeDevice(false);
    }
}

