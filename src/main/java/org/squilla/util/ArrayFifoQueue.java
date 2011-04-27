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
public class ArrayFifoQueue implements BlockingFifoQueue {

    private int maxElement;
    private int head;
    private int tail;
    private int size;
    private Object[] data;
    
    public ArrayFifoQueue(int maxSize) {
        maxElement = maxSize;
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
        if (size < maxElement) {
            return false;
        }
        return true;
    }
    
    protected void enqueueInternal(Object e) {
        data[tail] = e;
        tail = (tail + 1) % maxElement;
        size++;
    }
    
    public synchronized boolean enqueue(Object e) {
        if (isFull()) {
            return false;
        }
        if (e == null) {
            throw new NullPointerException();
        }
        enqueueInternal(e);
        notifyAll();
        return true;
    }
    
    protected Object dequeueInternal() {
        Object e = data[head];
        data[head] = null;
        head = (head + 1) % maxElement;
        size--;
        return e;
    }
    
    public synchronized Object dequeue() {
        if (isEmpty()) {
            return null;
        }
        Object e = dequeueInternal();
        notifyAll();
        return e;
    }
    
    public Object peek() {
        if (size == 0) {
            return null;
        }
        return data[head];
    }
    
    public synchronized boolean blockingEnqueue(Object e) {
        return blockingEnqueue(e, 0);
    }
    
    public synchronized boolean blockingEnqueue(Object e, int timeout) {
        while (isFull()) {
            try {
                wait(timeout);
            } catch (InterruptedException ex) {
            }
            if (timeout != 0 && isFull()) {
                return false;
            }
        }
        enqueueInternal(e);
        notifyAll();
        return true;
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
        Object e = dequeueInternal();
        notifyAll();
        return e;
    }

    public synchronized Object[] drainAll() {
        Object[] objs = new Object[size];
        int i = 0;
        while (!isEmpty()) {
            objs[i++] = dequeueInternal();
        }
        return objs;
    }
}
