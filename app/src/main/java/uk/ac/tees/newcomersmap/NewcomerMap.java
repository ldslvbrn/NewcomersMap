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
        if (onContentChangeListener != null) {
            onContentChangeListener.onContentChange(true);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        if (onContentChangeListener != null) {
            onContentChangeListener.onContentChange(true);
        }
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
        if (onContentChangeListener != null) {
            onContentChangeListener.onContentChange(true);
        }
    }

    public List<UserMarker> getMarkers() {
        return markers;
    }

    public void setMarkers(List<UserMarker> markers) {

        this.markers = new OnChangeCallableArrayList<>(markers);
        if (onContentChangeListener != null) {
            onContentChangeListener.onContentChange(true);
        }
    }

    @Exclude
    public void setOnContentChangeListener(OnContentChangeListener onContentChangeListener) {
        this.onContentChangeListener = onContentChangeListener;
    }

    @Exclude
    public void removeOnContentChangeListener(OnContentChangeListener onContentChangeListener) {
        if(this.onContentChangeListener == onContentChangeListener) {
            this.onContentChangeListener = null;
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
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            return super.set(index, element);
        }

        @Override
        public boolean add(E e) {
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            return super.add(e);
        }

        @Override
        public void add(int index, E element) {
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            super.add(index, element);
        }

        @Override
        public E remove(int index) {
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            return super.remove(index);
        }

        @Override
        public boolean remove(@Nullable Object o) {
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            return super.remove(o);
        }

        @Override
        public void clear() {
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            super.clear();
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends E> c) {
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            return super.addAll(c);
        }

        @Override
        public boolean addAll(int index, @NonNull Collection<? extends E> c) {
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            return super.addAll(index, c);
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            super.removeRange(fromIndex, toIndex);
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            return super.removeAll(c);
        }

        @Override
        public boolean retainAll(@NonNull java.util.Collection<?> c) {
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            return super.retainAll(c);
        }

        @Override
        public boolean removeIf(@NonNull Predicate<? super E> filter) {
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            return super.removeIf(filter);
        }

        @Override
        public void replaceAll(@NonNull UnaryOperator<E> operator) {
            if (onContentChangeListener != null) {
                onContentChangeListener.onContentChange(true);
            }
            super.replaceAll(operator);
        }
    }
}
