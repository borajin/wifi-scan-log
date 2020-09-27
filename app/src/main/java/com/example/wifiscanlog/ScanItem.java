package com.example.wifiscanlog;

public class ScanItem {
    private String name;
    private String apInfo;

    public ScanItem(String name, String apInfo) {
        this.name = name;
        this.apInfo = apInfo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApInfo() {
        return apInfo;
    }

    public void setApInfo(String apInfo) {
        this.apInfo = apInfo;
    }
}
