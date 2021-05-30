package model;

import android.net.Uri;

import java.sql.Timestamp;

public class Journal {
    private String title;
    private String thought;
    private String imageUrl;
    private Timestamp timestamp;
    private String userName;
    private String userId;

    public Journal() {
    }

    public Journal(String title, String thought, String imageUrl, Timestamp timestamp, String userName, String userId) {
        this.title = title;
        this.thought = thought;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.userName = userName;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
