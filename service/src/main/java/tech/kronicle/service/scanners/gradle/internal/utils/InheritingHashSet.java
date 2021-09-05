package tech.kronicle.service.scanners.gradle.internal.utils;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class InheritingHashSet<E> extends HashSet<E> {

    private final Set<E> parent;

    public InheritingHashSet() {
        parent = null;
    }

    private Set<E> union() {
        Set<E> union = new HashSet<>();
        super.iterator().forEachRemaining(union::add);
        if (nonNull(parent)) {
            parent.iterator().forEachRemaining(union::add);
        }
        return union;
    }

    @Override
    public int size() {
        return union().size();
    }

    @Override
    public boolean isEmpty() {
        if (!super.isEmpty()) {
            return false;
        }

        if (nonNull(parent)) {
            return parent.isEmpty();
        }

        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return union().iterator();
    }

    @Override
    public Spliterator<E> spliterator() {
        return union().spliterator();
    }
}
