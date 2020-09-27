package com.example.wifiscanlog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    private ListView scanItemList;
    private Button exportBtn;

    private ArrayAdapter arrayAdapter;
    private DBAdapter dbAdapter;
    private ArrayList<ScanItem> scanItems;
    private ArrayList<String> scanItems_str;

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

        dbAdapter = new DBAdapter(getContext());

        refresh();

        scanItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View clickedView, int position, long id) {
                String name = ((TextView)clickedView).getText().toString();
                delete_show(name);
            }

            void delete_show(final String name)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("삭제");
                builder.setMessage(name + "을 삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dbAdapter.open();
                                dbAdapter.delete(name);
                                dbAdapter.close();

                                refresh();

                                Toast.makeText(getContext(),"삭제완료",Toast.LENGTH_LONG).show();
                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();
            }
        });

        exportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveExcel();
            }
        });

        return layout;
    }

    public void refresh() {
        dbAdapter.open();

        scanItems = dbAdapter.get_all_scans();

        dbAdapter.close();

        scanItems_str = new ArrayList<>();
        for(int i=0; i<scanItems.size(); i++) {
            scanItems_str.add(scanItems.get(i).getName());
        }

        arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, scanItems_str.toArray());
        scanItemList.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
    }

    private void saveExcel() {
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row row = sheet.createRow(0);
        Cell cell;

        cell = row.createCell(0);
        cell.setCellValue("name");

        cell = row.createCell(1);
        cell.setCellValue("apInfo");

        for(int i=0; i<scanItems.size(); i++) {
            row = sheet.createRow(i+1);

            cell = row.createCell(0);
            cell.setCellValue(scanItems.get(i).getName());

            cell = row.createCell(1);
            cell.setCellValue(scanItems.get(i).getApInfo());
        }

        File xlsFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "WifiScanLog.xls");

        try {
            FileOutputStream os = new FileOutputStream(xlsFile);
            workbook.write(os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toast.makeText(getContext(),xlsFile.getAbsolutePath() + "에 저장했습니다.",Toast.LENGTH_LONG).show();
    }
}
