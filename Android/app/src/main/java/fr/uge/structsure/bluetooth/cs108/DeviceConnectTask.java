package fr.uge.structsure.bluetooth.cs108;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import fr.uge.structsure.MainActivity;

import com.csl.cslibrary4a.ReaderDevice;

import java.util.ArrayList;

public class DeviceConnectTask extends AsyncTask<Void, String, Integer> {
    boolean DEBUG = false;
    private final ReaderDevice connectingDevice;
    int waitTime;
    private int setting;

    DeviceConnectTask(ReaderDevice connectingDevice) {
        this.connectingDevice = connectingDevice;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.csLibrary4A.connect(connectingDevice);
        waitTime = 30;
        setting = -1;
    }

    @Override
    protected Integer doInBackground(Void... a) {
        do {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress("kkk ");
            if (MainActivity.csLibrary4A.isBleConnected()) {
                setting = 0; break;
            }
        } while (--waitTime > 0);
        if (setting != 0 || waitTime <= 0) {
            cancel(true);
        }
        publishProgress("mmm ");
        return waitTime;
    }

    @Override
    protected void onProgressUpdate(String... output) {
    }

    @Override
    protected void onCancelled(Integer result) {
        if (DEBUG) MainActivity.csLibrary4A.appendToLog("onCancelled(): setting = " + setting + ", waitTime = " + waitTime);
        if (setting >= 0) {
            System.out.println("TOAST - Setup problem after connection. Disconnect");
        } else {
            MainActivity.csLibrary4A.isBleConnected();
            System.out.println("TOAST - Unable to connect device");
        }
        super.onCancelled();
        MainActivity.csLibrary4A.disconnect(false);
    }

    protected void onPostExecute(Integer result) {
        if (DEBUG) MainActivity.csLibrary4A.appendToLog("onPostExecute(): setting = " + setting + ", waitTime = " + waitTime);
        connectingDevice.setConnected(true);
//        readerListAdapter.notifyDataSetChanged();

        System.out.println("TOAST - BLE is connected");


//        connectTimeMillis = System.currentTimeMillis();
        super.onPostExecute(result);
//        getActivity().onBackPressed();
    }
}
