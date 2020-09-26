package com.example.wifiscanlog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class WifiReceiver extends BroadcastReceiver {
    private WifiManager wifiManager;
    private ListView wifiDeviceList;

    public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList) {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        //broadcast 로 단말기의 상태변화나 다른 단말기가 송신하는 메세지를 receive 할 수 있고 그에 따른 처리도 가능함.
        //단말기 배터리가 부족하다거나 뭐 그런...

        //인텐트 쪽에서 scan 한 상태가 되면 scan 결과 처리하는 braodcast (개발자가 custom status 도 처리 할 수 있음)
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            List<ScanResult> wifiList = wifiManager.getScanResults();
            ArrayList<String> deviceList = new ArrayList<>();

            for (ScanResult scanResult : wifiList) {
                //scanresult feild 참고 - https://developer.android.com/reference/android/net/wifi/ScanResult?hl=ko
                //ssid, bssid(mac address), level(rssi), timestamp(언제 scan했는지), frequency 등..
                deviceList.add(scanResult.BSSID.replace(":", "") + ";" + scanResult.level);
            }

            //simple_list_item_1 은 안드로이드에서 기본적으로 제공해주는 listview item layout
            ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, deviceList.toArray());
            //listview 에 adapter 등록
            wifiDeviceList.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        } else {
            //참고 :: fail 시 첫 스캔이면 아무 것도 반환 안 하고 n번째 스캔이면 results 에 이전 결과가 출력됨.
            System.out.println("fail");
        }
    }
}

