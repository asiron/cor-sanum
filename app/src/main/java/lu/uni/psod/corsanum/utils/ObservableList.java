package lu.uni.psod.corsanum.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ObservableList<T> implements List<T> {

    private List<T> wrapped;
    private ArrayList<Listener<T>> listeners = new ArrayList<>();

    public ObservableList() {
        this.wrapped = new ArrayList<T>();
    }

    public ObservableList(List wrapped) {
        this.wrapped = wrapped;
    }

    public void addListener(Listener l) {
        listeners.add(l);
    }

    public void removeListener(Listener l) {
        listeners.remove(l);
    }

    @Override
    public int size() {
        return wrapped.size();
    }

    @Override
    public boolean isEmpty() {
        return wrapped.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return wrapped.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        final Iterator<T> iterator = wrapped.iterator();
        return new Iterator<T>() {
            T current = null;

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return current = iterator.next();
            }

            @Override
            public void remove() {
                iterator.remove();
                fireRemoved(current);
            }
        };
    }

    @Override
    public Object[] toArray() {
        return wrapped.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return wrapped.toArray(a);
    }

    @Override
    public boolean add(T e) {
        if (wrapped.add(e)) {
            fireAdded(e);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object o) {
        if (wrapped.remove(o)) {
            fireRemoved((T) o);
            return true;
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return wrapped.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (wrapped.addAll(c)) {
            fireAdded(c);
            return true;
        }

        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (wrapped.addAll(index, c)) {
            fireAdded(c);
        }

        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (wrapped.removeAll(c)) {
            fireRemoved((Collection<? extends T>) c);
            return true;
        }

        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (wrapped.retainAll(c)) {
            fireStructuralChange();
        }

        return false;
    }

    @Override
    public void clear() {
        wrapped.clear();
        fireStructuralChange();
    }

    @Override
    public boolean equals(Object o) {
        return wrapped.equals(o);
    }

    @Override
    public int hashCode() {
        return wrapped.hashCode();
    }

    @Override
    public T get(int index) {
        return wrapped.get(index);
    }

    @Override
    public T set(int index, T element) {
        T old = wrapped.set(index, element);
        fireRemoved(old);
        fireAdded(element);
        return old;
    }

    @Override
    public void add(int index, T element) {
        wrapped.add(index, element);
        fireAdded(element);
    }

    @Override
    public T remove(int index) {
        T old = wrapped.remove(index);
        fireRemovedSingle(index);
        return old;
    }

    @Override
    public int indexOf(Object o) {
        return wrapped.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return wrapped.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return wrapped.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return wrapped.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return wrapped.subList(fromIndex, toIndex);
    }

    private void fireRemovedSingle(int index) {
        for (Listener<T> l : listeners) {
            l.onSingleItemRemoved(this, index);
        }
    }

    private void fireRemoved(T... items) {
        fireRemoved(Arrays.asList(items));
    }

    private void fireRemoved(Collection<? extends T> asList) {
        for (Listener<T> l : listeners) {
            l.onItemsRemoved(this, asList);
        }
    }

    private void fireAdded(T... e) {
        fireAdded(Arrays.asList(e));
    }

    private void fireAdded(Collection<? extends T> asList) {
        for (Listener<T> l : listeners) {
            l.onItemsAdded(this, asList);
        }
    }

    private void fireStructuralChange() {
        for (Listener<T> l : listeners) {
            l.onStructuralChange(this);
        }
    }

    public void notifyItemChanged(int index) {
        for (Listener<T> l : listeners) {
            l.onItemChanged(this, index);
        }
    }

    public static interface Listener<T> {

        void onItemChanged(ObservableList<T> source, int index);

        void onSingleItemRemoved(ObservableList<T> source, int index);

        void onItemsAdded(ObservableList<T> source, Collection<? extends T> items);

        void onItemsRemoved(ObservableList<T> source, Collection<? extends T> items);

        void onStructuralChange(ObservableList<T> source);
    }
}
