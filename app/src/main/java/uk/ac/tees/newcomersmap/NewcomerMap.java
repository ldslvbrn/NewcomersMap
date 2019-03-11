package uk.ac.tees.newcomersmap;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NewcomerMap {

    private String documentId;
    private String title;
    private GeoPoint location;
    private List<UserMarker> markers;
    @Exclude
    private OnContentChangeListener onContentChangeListener;

    public NewcomerMap() {
        // No-args constructor required for Firestore db
    }

    public NewcomerMap(String documentId, String title, GeoPoint location, List<UserMarker> markers) {
        this.documentId = documentId;
        this.title = title;
        this.location = location;
        this.markers = markers;
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

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
        notifyObservers(true);
    }

    public List<UserMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<UserMarker> markers) {

        this.markers = new OnChangeCallableArrayList<>(markers);
        notifyObservers(true);
    }

    @Exclude
    public void setOnContentChangeListener(OnContentChangeListener onContentChangeListener) {
        this.onContentChangeListener = onContentChangeListener;
    }

    @Exclude
    public void removeOnContentChangeListener(OnContentChangeListener onContentChangeListener) {
        if(this.onContentChangeListener == onContentChangeListener) {
            this.onContentChangeListener = null;
            for(UserMarker marker : markers) {
                marker.removeOnContentChangeListener(onContentChangeListener);
            }
        }
    }

    @Exclude
    private void notifyObservers(boolean b) {
        if (onContentChangeListener != null) {
            onContentChangeListener.onContentChange(true);
        }
    }

    public interface OnContentChangeListener {
        void onContentChange(boolean b);
    }

    private class OnChangeCallableArrayList<E> extends ArrayList<E> {

        public OnChangeCallableArrayList(@NonNull Collection<? extends E> c) {
            super(c);
        }

        @Override
        public E set(int index, E element) {
            notifyObservers(true);
            return super.set(index, element);
        }

        @Override
        public boolean add(E e) {
            notifyObservers(true);
            return super.add(e);
        }

        @Override
        public void add(int index, E element) {
            notifyObservers(true);
            super.add(index, element);
        }

        @Override
        public E remove(int index) {
            notifyObservers(true);
            return super.remove(index);
        }

        @Override
        public boolean remove(@Nullable Object o) {
            notifyObservers(true);
            return super.remove(o);
        }

        @Override
        public void clear() {
            notifyObservers(true);
            super.clear();
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends E> c) {
            notifyObservers(true);
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, @NonNull Collection<? extends E> c) {
            notifyObservers(true);
            return super.addAll(index, c);
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            notifyObservers(true);
            super.removeRange(fromIndex, toIndex);
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            notifyObservers(true);
            return super.removeAll(c);
        }

        @Override
        public boolean retainAll(@NonNull java.util.Collection<?> c) {
            notifyObservers(true);
            return super.retainAll(c);
        }

        @Override
        public boolean removeIf(@NonNull Predicate<? super E> filter) {
            notifyObservers(true);
            return super.removeIf(filter);
        }

        @Override
        public void replaceAll(@NonNull UnaryOperator<E> operator) {
            notifyObservers(true);
            super.replaceAll(operator);
        }
    }
}
