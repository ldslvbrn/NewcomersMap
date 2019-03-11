package uk.ac.tees.newcomersmap;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

public class UserMarker {
    private String documentId, title, description;
    private GeoPoint location;
    @Exclude
    private NewcomerMap.OnContentChangeListener onContentChangeListener;

    public UserMarker() {
        // No-args constructor required for Firestore db
    }

    public UserMarker(String documentId, String title, String description, GeoPoint location) {
        this.documentId = documentId;
        this.title = title;
        this.description = description;
        this.location = location;
    }

    public String getDocumentId() {
        return documentId;
    }

    @Exclude
    public void setDocumentId(String documentId) {
        this.documentId = documentId;
        notifyObservers(true);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyObservers(true);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyObservers(true);
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
        notifyObservers(true);
    }

    @Exclude
    public void setOnContentChangeListener(NewcomerMap.OnContentChangeListener onContentChangeListener) {
        this.onContentChangeListener = onContentChangeListener;
    }

    @Exclude
    public void removeOnContentChangeListener(NewcomerMap.OnContentChangeListener onContentChangeListener) {
        if(this.onContentChangeListener == onContentChangeListener) {
            this.onContentChangeListener = null;
        }
    }

    @Exclude
    private void notifyObservers(boolean b) {
        if (onContentChangeListener != null) {
            onContentChangeListener.onContentChange(true);
        }
    }
}
