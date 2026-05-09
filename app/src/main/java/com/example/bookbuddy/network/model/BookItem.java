package com.example.bookbuddy.network.model;

import com.google.gson.annotations.SerializedName;

public class BookItem {
    @SerializedName("id")
    private String id;

    @SerializedName("volumeInfo")
    private VolumeInfo volumeInfo;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public VolumeInfo getVolumeInfo() { return volumeInfo; }
    public void setVolumeInfo(VolumeInfo volumeInfo) { this.volumeInfo = volumeInfo; }
}
