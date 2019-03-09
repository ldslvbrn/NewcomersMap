//package uk.ac.tees.newcomersmap;
//
//import com.google.firebase.firestore.GeoPoint;
//
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.function.Predicate;
//import java.util.function.UnaryOperator;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//
//public class MapValidatorAdapter extends NewcomerMap {
//
//    private boolean valueChanged = false;
//
////    public MapValidatorAdapter(NewcomerMap newcomerMap) {
////        super.setDocumentId(newcomerMap.getDocumentId());
////        super.setTitle(newcomerMap.getTitle());
////        super.setLocation(newcomerMap.getLocation());
////        ObservableArrayList list = new ObservableArrayList(newcomerMap.getMarkers());
////        super.setMarkers(list);
////    }
//
//    public boolean hasValueChanged() {
//        return valueChanged;
//    }
//
//    @Override
//    public void setDocumentId(String documentId) {
//        super.setDocumentId(documentId);
//        valueChanged = true;
//    }
//
//    @Override
//    public void setTitle(String title) {
//        super.setTitle(title);
//        valueChanged = true;
//    }
//
//    @Override
//    public void setLocation(GeoPoint location) {
//        super.setLocation(location);
//        valueChanged = true;
//    }
//
//    @Override
//    public void setMarkers(List<UserMarker> markers) {
//        super.setMarkers(markers);
//        valueChanged = true;
//    }
//
//    private class ObservableArrayList<E> extends ArrayList<E> {
//
//        public ObservableArrayList(@NonNull Collection<? extends E> c) {
//            super(c);
//        }
//
//        @Override
//        public E set(int index, E element) {
//            valueChanged = true;
//            return super.set(index, element);
//        }
//
//        @Override
//        public boolean add(E e) {
//            valueChanged = true;
//            return super.add(e);
//        }
//
//        @Override
//        public void add(int index, E element) {
//            valueChanged = true;
//            super.add(index, element);
//        }
//
//        @Override
//        public E remove(int index) {
//            valueChanged = true;
//            return super.remove(index);
//        }
//
//        @Override
//        public boolean remove(@Nullable Object o) {
//            valueChanged = true;
//            return super.remove(o);
//        }
//
//        @Override
//        public void clear() {
//            valueChanged = true;
//            super.clear();
//        }
//
//        @Override
//        public boolean addAll(@NonNull Collection<? extends E> c) {
//            valueChanged = true;
//            return super.addAll(c);
//        }
//
//        @Override
//        public boolean addAll(int index, @NonNull Collection<? extends E> c) {
//            valueChanged = true;
//            return super.addAll(index, c);
//        }
//
//        @Override
//        protected void removeRange(int fromIndex, int toIndex) {
//            valueChanged = true;
//            super.removeRange(fromIndex, toIndex);
//        }
//
//        @Override
//        public boolean removeAll(@NonNull Collection<?> c) {
//            valueChanged = true;
//            return super.removeAll(c);
//        }
//
//        @Override
//        public boolean retainAll(@NonNull java.util.Collection<?> c) {
//            valueChanged = true;
//            return super.retainAll(c);
//        }
//
//        @Override
//        public boolean removeIf(@NonNull Predicate<? super E> filter) {
//            valueChanged = true;
//            return super.removeIf(filter);
//        }
//
//        @Override
//        public void replaceAll(@NonNull UnaryOperator<E> operator) {
//            valueChanged = true;
//            super.replaceAll(operator);
//        }
//    }
//}
