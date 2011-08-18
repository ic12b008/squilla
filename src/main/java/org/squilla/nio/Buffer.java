/*
 * Copyright 2011 Shotaro Uchida <fantom@xmaker.mx>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.squilla.nio;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public abstract class Buffer {

    protected static final int NON_MARK = -1;
    private int mark;
    private int position;
    private int limit;
    private int capacity;

    protected Buffer(int mark, int position, int limit, int capacity) {
        this.capacity = capacity;
        limit(limit);
        position(position);
    }

    public final int capacity() {
        return capacity;
    }

    public final int position() {
        return position;
    }

    public final Buffer position(int newPosition) {
        if (mark > newPosition) {
            mark = NON_MARK;
        }
        if (newPosition > limit) {
            throw new IllegalArgumentException("Position Out of range");
        }
        this.position = newPosition;
        return this;
    }

    public final int limit() {
        return limit;
    }

    public final Buffer limit(int newLimit) {
        if (position > newLimit) {
            position = newLimit;
        }
        if (newLimit > capacity) {
            throw new IllegalArgumentException("Limit Out of range");
        }
        this.limit = newLimit;
        return this;
    }

    public final Buffer skip(int n) {
        position += n;
        return this;
    }

    public final Buffer mark() {
        mark = position;
        return this;
    }
    
    public final Buffer reset() {
        if (mark == NON_MARK) {
            throw new InvalidMarkException();
        }
        position = mark;
        return this;
    }
    
    public final Buffer clear() {
        mark = NON_MARK;
        position = 0;
        limit = capacity;
        return this;
    }
    
    public final Buffer flip() {
        limit = position;
        return rewind();
    }

    public final Buffer rewind() {
        position = 0;
        mark = NON_MARK;
        return this;
    }

    public final int remaining() {
        return limit - position;
    }

    public final boolean hasRemaining() {
        return remaining() > 0;
    }

    public abstract boolean isReadOnly();
    
//    public abstract boolean hasArray();
//    
//    public abstract Object array();
//    
//    public abstract int arrayOffset();
//    
//    public abstract boolean isDirect();
    
}
