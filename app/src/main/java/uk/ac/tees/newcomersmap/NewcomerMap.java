package uk.ac.tees.newcomersmap.data;

import com.google.api.client.util.DateTime;
import com.google.firebase.firestore.Exclude;

public class NewcomerMap {

    private String documentId, title, description;
    private double longitude, latitude;
    private DateTime lastModified;

    public NewcomerMap() {
        // No-args constructor required for Firebase
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public DateTime getLastModified() {
        return lastModified;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLastModified(DateTime lastModified) {
        this.lastModified = lastModified;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
