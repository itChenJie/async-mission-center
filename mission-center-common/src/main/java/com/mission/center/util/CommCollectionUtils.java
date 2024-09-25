package com.mission.center.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CommCollectionUtils {

    public static <T> List<T> newArrayList(T... elements) {
        checkNotNull(elements);
        int capacity = computeArrayListCapacity(elements.length);
        List<T> list = new ArrayList<>(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    static int computeArrayListCapacity(int arraySize) {
        if (arraySize < 0) {
            throw new IllegalArgumentException("arrayList cannot be negative but was: " + arraySize);
        }
        return saturatedCast(5L + arraySize + (arraySize / 10));
    }

    private static int saturatedCast(long value) {
        if (value > 2147483647L) {
            return 2147483647;
        } else {
            return value < -2147483648L ? -2147483648 : (int) value;
        }
    }

    private static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        } else {
            return reference;
        }
    }
}
