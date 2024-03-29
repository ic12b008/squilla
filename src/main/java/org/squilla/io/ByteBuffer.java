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
package org.squilla.io;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class ByteBuffer extends Buffer {

    private int offset;
    private byte[] buffer;

    public ByteBuffer(byte[] buffer, int offset, int length) {
        super(length);
        this.buffer = buffer;
        this.offset = offset;
    }

    public ByteBuffer(byte[] buffer) {
        this(buffer, 0, buffer.length);
    }
    
    public String toString() {
        return ByteUtil.toString(buffer, offset, getPosition());
    }

    public int getOffset() {
        return offset;
    }

    public byte[] getRawArray() {
        return buffer;
    }
    
    public ByteBuffer extend(int newSize) {
        byte[] newArray = new byte[newSize];
        System.arraycopy(buffer, offset, newArray, 0, getCapacity());
        offset = 0;
        buffer = newArray;
        return this;
    }

    public ByteBuffer put(byte src) {
        buffer[offset + getPosition()] = src;
        skip(1);
        return this;
    }

    public ByteBuffer put(byte[] src) {
        System.arraycopy(src, offset, buffer, offset + getPosition(), src.length);
        skip(src.length);
        return this;
    }

    public ByteBuffer put(byte[] src, int srcOff, int srcLen) {
        System.arraycopy(src, srcOff, buffer, offset + getPosition(), srcLen);
        skip(srcLen);
        return this;
    }

    public ByteBuffer clean(int length) {
        for (int i = 0; i < length; i++) {
            put((byte) 0);
        }
        return this;
    }
    
    public byte peek() {
        if (getRemaining() < 1) {
            throw new BufferUnderflowException();
        }
        return buffer[offset + getPosition()];
    }

    public byte get() {
        if (getRemaining() < 1) {
            throw new BufferUnderflowException();
        }
        byte b = buffer[offset + getPosition()];
        skip(1);
        return b;
    }

    public ByteBuffer get(byte[] dst) {
        if (getRemaining() < dst.length) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(buffer, offset + getPosition(), dst, 0, dst.length);
        skip(dst.length);
        return this;
    }

    public ByteBuffer get(byte[] dst, int dstOff, int dstLen) {
        if (getRemaining() < dstLen) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(buffer, offset + getPosition(), dst, dstOff, dstLen);
        skip(dstLen);
        return this;
    }
    
    public byte[] getByteArray(int octets) {
        byte[] dst = new byte[octets];
        get(dst);
        return dst;
    }
}
