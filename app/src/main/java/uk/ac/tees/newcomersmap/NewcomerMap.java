package uk.ac.tees.newcomersmap;

import com.google.firebase.firestore.Exclude;

import java.util.List;

public class NewcomerMap {

    private String documentId, title, description;
    private double longitude, latitude;
    private List<UserMarker> markers;

    public NewcomerMap() {
        // No-args constructor required for Firebase Firestore db
    }

    public NewcomerMap(String documentId, String title, String description, double longitude, double latitude, List<UserMarker> markers) {
        this.documentId = documentId;
        this.title = title;
        this.description = description;
        this.longitude = longitude;
        this.latitude = latitude;
        this.markers = markers;
    }

    public List<UserMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<UserMarker> markers) {
        this.markers = markers;
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

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
