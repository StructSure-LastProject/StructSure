package fr.uge.structsure.bluetooth.cs108.java;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

public class TagsIndex implements Comparable<TagsIndex> {
    private String address;
    private int position;

    @Keep
    public TagsIndex(String address, int position) {
        this.address = address;
        this.position = position;
    }

    @Keep public String getAddress() {
        return address;
    }
    @Keep public int getPosition() {
        return position;
    }

    @Override
    public int compareTo(@NonNull TagsIndex tagsIndex) {
        return address.compareTo(tagsIndex.address);
    }
}