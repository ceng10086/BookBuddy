package com.example.bookbuddy.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VolumeInfo {
    @SerializedName("title")
    private String title;

    @SerializedName("authors")
    private List<String> authors;

    @SerializedName("publisher")
    private String publisher;

    @SerializedName("publishedDate")
    private String publishedDate;

    @SerializedName("description")
    private String description;

    @SerializedName("pageCount")
    private int pageCount;

    @SerializedName("imageLinks")
    private ImageLinks imageLinks;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<String> getAuthors() { return authors; }
    public void setAuthors(List<String> authors) { this.authors = authors; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getPublishedDate() { return publishedDate; }
    public void setPublishedDate(String publishedDate) { this.publishedDate = publishedDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getPageCount() { return pageCount; }
    public void setPageCount(int pageCount) { this.pageCount = pageCount; }

    public ImageLinks getImageLinks() { return imageLinks; }
    public void setImageLinks(ImageLinks imageLinks) { this.imageLinks = imageLinks; }

    public String getFirstAuthor() {
        if (authors != null && !authors.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < authors.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(authors.get(i));
            }
            return sb.toString();
        }
        return "未知作者";
    }

    public String getThumbnailUrl() {
        if (imageLinks != null) {
            if (imageLinks.getThumbnail() != null) {
                return imageLinks.getThumbnail().replace("http://", "https://");
            }
        }
        return null;
    }
}
