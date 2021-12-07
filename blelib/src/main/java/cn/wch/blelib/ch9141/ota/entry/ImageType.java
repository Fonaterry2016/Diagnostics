package cn.wch.blelib.ch9141.ota.entry;

import androidx.annotation.NonNull;

public enum ImageType {
    A("A"),
    B("B"),
    C("B"),
    UNKNOWN("UNKNOWN");

    private String description;
    ImageType(String s) {
        description=s;
    }

    @NonNull
    @Override
    public String toString() {
        return description;
    }
}
