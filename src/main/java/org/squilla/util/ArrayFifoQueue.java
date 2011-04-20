/*
 * Copyright 2011 Shotaro Uchida <fantom@xmaker.mx>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.squilla.util;

/**
 * Array implementation of FIFO Queue.
 * 
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class ArrayFifoQueue implements FifoQueue {

    private int capacity;
    private int size;
    private Object[] data;
    
    public ArrayFifoQueue(int maxSize) {
        capacity = maxSize;
        size = 0;
        data = new Object[maxSize];
    }
    
    public synchronized int size() {
        return size;
    }
    
    public synchronized boolean isEmpty() {
        if (size == 0) {
            return true;
        }
        return false;
    }
    
    public synchronized boolean isFull() {
        if (size < capacity) {
            return false;
        }
        return true;
    }
    
    public synchronized boolean enqueue(Object e) {
        if (isFull()) {
            return false;
        }
        if (e == null) {
            throw new NullPointerException();
        }
        data[size++] = e;
        notifyAll();
        return true;
    }
    
    public synchronized Object dequeue() {
        if (isEmpty()) {
            return null;
        }
        size--;
        return data[size];
    }
    
    public synchronized Object peek() {
        if (isEmpty()) {
            return null;
        }
        return data[size - 1];
    }

    public synchronized Object blockingDequeue() {
        return this.blockingDequeue(0);
    }

    public synchronized Object blockingDequeue(int timeout) {
        while (isEmpty()) {
            try {
                wait(timeout);
            } catch (InterruptedException ex) {
            }
            if (timeout != 0 && isEmpty()) {
                return null;
            }
        }
        size--;
        return data[size];
    }

    public Object[] drainAll() {
        Object[] objs = new Object[size];
        for (int i = 0; i < objs.length; i++) {
            size--;
            objs[i] = data[size];
        }
        return objs;
    }
}
