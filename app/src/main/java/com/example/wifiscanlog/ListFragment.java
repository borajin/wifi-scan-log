package com.example.wifiscanlog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private ListView scanItemList;
    private Button exportBtn;

    private ArrayAdapter arrayAdapter;
    private ArrayList<String> scanItems;

    public ListFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_info, container, false);

        exportBtn = layout.findViewById(R.id.exportBtn);
        scanItemList = layout.findViewById(R.id.scanItemList);

        //simple_list_item_1 은 안드로이드에서 기본적으로 제공해주는 listview item layout
        //arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, scanItems.toArray());
        //listview 에 adapter 등록
        //scanItemList.setAdapter(arrayAdapter);

        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayAdapter.notifyDataSetChanged();
            }
        });

        return layout;
    }
}
