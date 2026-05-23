package com.chat.shared.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CircularBuffer<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final T[] buffer;
    private int head = 0;
    private int tail = 0;
    private int count = 0;

    @SuppressWarnings("unchecked")
    public CircularBuffer(int capacity) {
        buffer = (T[]) new Object[capacity];
    }

    public synchronized void add(T element) {
        buffer[tail] = element;
        tail = (tail + 1) % buffer.length;
        if (count < buffer.length) {
            count++;
        } else {
            head = (head + 1) % buffer.length;
        }
    }

    public synchronized List<T> toList() {
        List<T> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            list.add(buffer[(head + i) % buffer.length]);
        }
        return list;
    }
}