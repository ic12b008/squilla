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
public abstract class ByteBuffer extends Buffer {

    protected int offset;
    protected byte[] array = null;
    private ByteOrder order = ByteOrder.BIG_ENDIAN;
    
    public static ByteBuffer allocateDirect(int capacity) {
        return allocate(capacity);
    }
    
    public static ByteBuffer allocate(int capacity) {
        ByteBuffer byteBuffer = new FrameBuffer(NON_MARK, 0, capacity, capacity);
        byteBuffer.array = new byte[capacity];
        byteBuffer.offset = 0;
        return byteBuffer;
    }
    
    public static ByteBuffer wrap(byte[] array, int offset, int length) {
        ByteBuffer byteBuffer = new FrameBuffer(NON_MARK, offset, offset + length, array.length);
        byteBuffer.array = array;
        byteBuffer.offset = 0;
        return byteBuffer;
    }
    
    public static ByteBuffer wrap(byte[] array) {
        ByteBuffer byteBuffer = new FrameBuffer(NON_MARK, 0, array.length, array.length);
        byteBuffer.array = array;
        byteBuffer.offset = 0;
        return byteBuffer;
    }
    
    public abstract ByteBuffer slice();
    
    public abstract ByteBuffer duplicate();

    public abstract ByteBuffer asReadOnlyBuffer();
    
    protected ByteBuffer(int mark, int position, int limit, int capacity) {
        super(mark, position, limit, capacity);
    }
    
    public abstract byte get();
    
    public abstract ByteBuffer put(byte b);
    
    public abstract byte get(int index);
    
    public abstract ByteBuffer put(int index, byte b);
    
    public ByteBuffer get(byte[] dst, int offset, int length) {
        for (int i = offset; i < offset + length; i++) {
             dst[i] = get();
        }
        return this;
    }
    
    public ByteBuffer get(byte[] dst) {
        return get(dst, 0, dst.length);
    }
    
    public ByteBuffer put(ByteBuffer src) {
        while (src.hasRemaining()) {
            put(src.get());
        }
        return this;
    }
    
    public ByteBuffer put(byte[] src, int offset, int length) {
        for (int i = offset; i < offset + length; i++) {
            put(src[i]);
        }
        return this;
    }
    
    public final ByteBuffer put(byte[] src) {
        return put(src, 0, src.length);
    }
    
    public final boolean hasArray() {
        return array != null;
    }
    
    public final byte[] array() {
        return array;
    }
    
    public final int arrayOffset() {
        return offset;
    }

    public abstract ByteBuffer compact();
    
    public abstract boolean isDirect();
    
    public String toString() {
        return super.toString();
    }
    
    public int hashCode() {
        int hash = 1;
        int pos = position();
        int lim = limit();
        for (int i = pos; i < lim - 1; i++) {
            hash = 31 * hash + get(i);
        }
        return hash;
    }
    
    public boolean equals(Object ob) {
        if (!(ob instanceof ByteBuffer)) {
            return false;
        }
        ByteBuffer that = (ByteBuffer) ob;
        if (that.remaining() != this.remaining()) {
            return false;
        }
        int pos1 = this.position();
        int lim1 = this.limit();
        int pos2 = that.position();
        int lim2 = that.limit();
        for (int i1 = pos1, i2 = pos2; (i1 < lim1 - 1) && (i2 < lim2 - 1); i1++, i2++) {
            int e1 = this.get(i1);
            int e2 = that.get(i2);
            if (e1 != e2) {
                return false;
            }
        }
        return true;
    }
    
    public final ByteOrder order() {
        return order;
    }
    
    public final ByteBuffer order(ByteOrder bo) {
        if (bo != ByteOrder.BIG_ENDIAN && bo != ByteOrder.LITTLE_ENDIAN) {
            throw new UnsupportedOperationException("Unknown ByteOrder object");
        }
        this.order = bo;
        return this;
    }
}
