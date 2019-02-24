package uk.ac.tees.newcomersmap;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class NewcomerMap {

    private String documentId, title, description;
    private GeoPoint location;
    private List<UserMarker> markers;

    public NewcomerMap() {
        // No-args constructor required for Firebase Firestore db
    }

    public NewcomerMap(String documentId, String title, String description, GeoPoint location, List<UserMarker> markers) {
        this.documentId = documentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.markers = markers;
    }

    public String getDocumentId() {
        return documentId;
    }

    @Exclude
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public List<UserMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<UserMarker> markers) {
        this.markers = markers;
    }
}
