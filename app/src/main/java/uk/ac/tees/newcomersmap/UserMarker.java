package uk.ac.tees.newcomersmap;

import com.google.firebase.firestore.Exclude;

public class UserMarker {
    private String documentId, title;
    private long longitude, latitude;

    public UserMarker() {
        // No-args constructor required for Firebase Firestore db
    }

    public UserMarker(String documentId, String title, long longitude, long latitude) {
        this.documentId = documentId;
        this.title = title;
        this.longitude = longitude;
        this.latitude = latitude;
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

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }

    public long getLatitude() {
        return latitude;
    }

    public void setLatitude(long latitude) {
        this.latitude = latitude;
    }
}
