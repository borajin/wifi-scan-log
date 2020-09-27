package com.example.wifiscanlog;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ScanFragment extends Fragment
{
    private ListView wifiList;
    private WifiManager wifiManager;
    private WifiReceiver receiverWifi;
    private Button scanBtn;
    private Button listBtn;
    private EditText editListName;
    private DBAdapter dbAdapter;

    public ScanFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_scan, container, false);

        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiList = layout.findViewById(R.id.scanItemList);
        scanBtn = layout.findViewById(R.id.exportBtn);
        listBtn = layout.findViewById(R.id.listBtn);
        editListName = layout.findViewById(R.id.editListName);

        scanBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiManager.startScan();
            }
        });

        listBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editListName.getText().toString();

                if(name.equals("")) {
                    Toast.makeText(getContext(), "공백은 입력할 수 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    saveList(name);
                }
            }
        });

        return layout;
    }

    private void saveList(String name) {
        StringBuilder apInfo = new StringBuilder("");

        if(wifiList.getAdapter() == null || wifiList.getAdapter().getCount() <= 0) {
            Toast.makeText(getContext(), "스캔 결과가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i=0; i < wifiList.getAdapter().getCount(); i++) {
            apInfo.append(wifiList.getAdapter().getItem(i).toString());
            apInfo.append("/");
        }

        dbAdapter = new DBAdapter(getContext());
        dbAdapter.open();

        dbAdapter.insert(name, apInfo.toString());

        dbAdapter.close();

        editListName.setText("");
        Toast.makeText(getContext(), name + " ap info 저장", Toast.LENGTH_SHORT).show();
    }

    //마지막 초기화 작업?? onresume은 activity가 전면에 나타날 때, oncreate 호출 이후에도 호출됨.
    @Override
    public void onResume() {
        super.onResume();
        receiverWifi = new WifiReceiver(wifiManager, wifiList);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getActivity().registerReceiver(receiverWifi, intentFilter);
    }

    //onStop, onDestroy 호출되기 이전에 호출됨. onresume 쌍으로 보고 거기서 했던 작업을 여기서 정리, 멈춤.
    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiverWifi);
    }
}