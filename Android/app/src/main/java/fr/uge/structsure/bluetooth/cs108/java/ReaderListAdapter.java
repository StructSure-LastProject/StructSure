package fr.uge.structsure.bluetooth.cs108.java;

import static java.sql.DriverManager.println;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.csl.cslibrary4a.ReaderDevice;

import java.util.ArrayList;

import fr.uge.structsure.MainActivity;

public class ReaderListAdapter extends ArrayAdapter<ReaderDevice> {
    final boolean DEBUG = false;
    private final Context context;
    private final int resourceId;
    private final ArrayList<ReaderDevice> readersList;
    private boolean select4detail, select4Rssi, selectDupElim, select4Extra1, select4Extra2;

    public ReaderListAdapter(Context context, int resourceId, ArrayList<ReaderDevice> readersList, boolean select4detail, boolean select4Rssi) {
        super(context, resourceId, readersList);
        this.context = context;
        this.resourceId = resourceId;
        this.readersList = readersList;
        this.select4detail = select4detail;
        this.select4Rssi = select4Rssi;
        select4Extra1 = false;
        select4Extra2 = false;
    }

    public ReaderListAdapter(Context context, int resourceId, ArrayList<ReaderDevice> readersList, boolean select4detail, boolean select4Rssi, boolean selectDupElim, boolean select4Extra1, boolean select4Extra2) {
        super(context, resourceId, readersList);
        this.context = context;
        this.resourceId = resourceId;
        this.readersList = readersList;
        this.select4detail = select4detail;
        this.select4Rssi = select4Rssi;
        this.selectDupElim = selectDupElim;
        this.select4Extra1 = select4Extra1;
        this.select4Extra2 = select4Extra2;
        MainActivity.csLibrary4A.appendToLog("select4Extra1 = " + select4Extra1 + ", select4Extra2 = " + select4Extra2);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReaderDevice reader = readersList.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(resourceId, null);
        }

        String text1 = "";
        if (reader.getName() != null) {
            if (reader.getName().length() != 0) {
                text1 += reader.getName();
            }
        }
        if (reader.getAddress() != null) {
            if (reader.getAddress().length() != 0) {
                if (text1.length() != 0) text1 += "\n";
                text1 += reader.getAddress();
            }
        }
        if (reader.getUpcSerial() != null) {
            if (reader.getUpcSerial().length() != 0)  {
                if (text1.length() != 0) text1 += "\n";
                text1 += reader.getUpcSerial();
            }
        }
        if (MainActivity.csLibrary4A.isBleScanning()) {
            if (reader.getServiceUUID2p1() == 0) text1 += "\nCS108 Reader";
            else if (reader.getServiceUUID2p1() == 2) text1 += "\nCS710S Reader";
        }
        println("ReaderListAdapter: setText(" + text1 + ")");

        println("ReaderListAdapter: setCount(" + reader.getCount() + ")");

        if (select4Rssi) {
            double rssiValue = reader.getRssi();
            if (MainActivity.csLibrary4A.getRssiDisplaySetting() != 0 && rssiValue > 0)
                rssiValue -= MainActivity.csLibrary4A.dBuV_dBm_constant;
            println("ReaderListAdapter: setRssi(" + String.format("%.1f", rssiValue) + ")");
        }

        if (select4Extra1) {
            int portValue = reader.getPort() + 1;
            println("ReaderListAdapter: setPort(" + portValue + ")");
        }

        if (select4Extra2) {
            int codeStatus = reader.getStatus();
            int codeSensor = reader.getCodeSensor(); int codeRssi = reader.getCodeRssi(); float codeTempC = reader.getCodeTempC();
            String brand = reader.getBrand();
            String strExtra = "";
            if (codeStatus > reader.INVALID_STATUS) { //for Bap tags
                int portstatus = reader.getStatus(); if (portstatus > reader.INVALID_STATUS) {
                    if ((portstatus & 2) == 0) strExtra += "Bat OK";
                    else strExtra += "Bat NG";
                    if ((portstatus & 4) != 0) strExtra += "\nTemper NG";
                }
            } else if (codeSensor > reader.INVALID_CODESENSOR && codeRssi > reader.INVALID_CODERSSI) { //for Axzon/Magnus tags
                strExtra = "SC=" + String.format("%d", codeSensor);
                int iHumidityThreshold = 0; // PATCH
                if (false && reader.getCodeSensorMax() > 0) {
                    float fValue = (float) codeSensor;
                    fValue /= (float) reader.getCodeSensorMax();
                    fValue *= 100;
                    strExtra += "\nSC=" + String.format("%.1f", fValue) + "%";
                } else if (iHumidityThreshold > 0) {
                    strExtra += "\nSC=" + (codeSensor >= iHumidityThreshold ? "Dry" : "Wet");
                }
                int ocrssiMin = -1; int ocrssiMax = -1; boolean bValidOcrssi = false;
                ocrssiMax = Integer.MAX_VALUE; // PATCH
                ocrssiMin = 0; // PATCH
                if (ocrssiMax > 0 && ocrssiMin > 0 && (codeRssi > ocrssiMax || codeRssi < ocrssiMin)) strExtra += ("\n<font color=red>OCRSSI=" + String.format("%d", codeRssi) + "</font>");
                else {
                    bValidOcrssi = true; strExtra += ("\nOCRSSI=" + String.format("%d", codeRssi));
                }
                int backport = reader.getBackport1(); if (backport > reader.INVALID_BACKPORT) strExtra += String.format("\nBP1=%d", backport);
                backport = reader.getBackport2(); if (backport > reader.INVALID_BACKPORT) strExtra += String.format("\nBP2=%d", backport);
            } else if (codeTempC > reader.INVALID_CODETEMPC) { //for Ctesius tags
                strExtra = ("T=" + String.format("%.1f", codeTempC) + (char) 0x00B0 + "C");
            } else if (brand != null) { //reader.getDetails().contains("E2806894")) { //for code8 tags
                strExtra = ((brand != null) ? ("Brand=" + brand) : "");
            } else if (reader.getSensorData() < reader.INVALID_SENSORDATA) {
                strExtra = "SD=" + String.valueOf(reader.getSensorData());
            }
            println("ReaderListAdapter: setPortText(" + Html.fromHtml(strExtra) + ")");
        }

        if (reader.isConnected() || select4detail == false) {
            println("ReaderListAdapter: setReaderDetailA(" + reader.getDetails() + ")");
            if (reader.isConnected()) {
                println("ReaderListAdapter: setReaderDetailB(Connected)");
            } else {
                int channel = reader.getChannel();
                int phase = reader.getPhase();
                String stringDetailB = null;
                if (channel != 0 || phase != 0) {
                    double dChannel = MainActivity.csLibrary4A.getLogicalChannel2PhysicalFreq(reader.getChannel());
                    stringDetailB = "Phase=" + phase + "\n" + dChannel + "MHz";
                }
                if (stringDetailB != null) println("ReaderListAdapter: setReaderDetailB(" + stringDetailB + ")");
            }
        }
        return convertView;
    }

    public boolean getSelectDupElim() { return selectDupElim; }
    public void setSelectDupElim(boolean selectDupElim) { this.selectDupElim = selectDupElim; }
}
