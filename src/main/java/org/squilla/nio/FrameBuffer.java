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
public class FrameBuffer extends ByteBuffer {

    private boolean readOnly = false;
    
    protected FrameBuffer(int mark, int position, int limit, int capacity) {
        super(mark, position, limit, capacity);
    }
    
    public ByteBuffer slice() {
        throw new UnsupportedOperationException();
    }
    
    public ByteBuffer duplicate() {
        throw new UnsupportedOperationException();
    }

    public ByteBuffer asReadOnlyBuffer() {
        throw new UnsupportedOperationException();
    }
    
    private int checkPosition() {
        int pos = position();
        if (pos > limit()) {
            throw new BufferUnderflowException();
        }
        return pos;
    }
    
    public byte get() {
        byte b = get(checkPosition());
        skip(1);
        return b;
    }
    
    public byte get(int index) {
        return array[offset + index];
    }
    
    public byte peek() {
        return get(checkPosition());
    }
    
    public ByteBuffer put(byte src) {
        put(checkPosition(), src);
        skip(1);                                            
        return this;
    }
    
    public ByteBuffer put(int index, byte b) {
        if (readOnly) {
            throw new ReadOnlyBufferException();
        }
        array[offset + index] = b;
        return this;
    }
    
    public ByteBuffer get(byte[] dst, int dstOff, int dstLen) {
        if (remaining() < dstLen) {
            throw new BufferUnderflowException();
        }
        System.arraycopy(array, offset + position(), dst, dstOff, dstLen);
        skip(dstLen);
        return this;
    }
    
    public ByteBuffer put(byte[] src, int srcOff, int srcLen) {
        if (readOnly) {
            throw new ReadOnlyBufferException();
        }
        if (remaining() < srcLen) {
            throw new BufferOverflowException();
        }
        System.arraycopy(src, srcOff, array, offset + position(), srcLen);
        skip(srcLen);
        return this;
    }

    public ByteBuffer compact() {
        throw new UnsupportedOperationException();
    }
    
    public boolean isDirect() {
        return false;
    }
    
    public void clean(int length) {
        for (int i = 0; i < length; i++) {
            put((byte) 0);
        }
    }
    
    public int getOctetInt(int octet) {
        if (remaining() < octet) {
            throw new BufferUnderflowException();
        }
        int i = getOctetInt(octet, position());
        skip(octet);
        return i;
    }
    
    public int getOctetInt(int octet, int index) {
        return ByteUtil.getByteUtil(order()).toInt(array, offset + index, octet);
    }
    
    public FrameBuffer putOctetInt(int octet, int value) {
        if (remaining() < octet) {
            throw new BufferOverflowException();
        }
        putOctetInt(octet, position(), value);
        skip(octet);
        return this;
    }
    
    public FrameBuffer putOctetInt(int octet, int index, int value) {
        if (readOnly) {
            throw new ReadOnlyBufferException();
        }
        ByteUtil.getByteUtil(order()).toByteArray(value, octet, array, offset + index);
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
    
    public FrameBuffer putInt8(int i) {
        return (FrameBuffer) put((byte) (i & 0xFF));
    }

    public FrameBuffer putInt16(int i) {
        return putOctetInt(ByteUtil.INT_16_SIZE, i);
    }

    public FrameBuffer putInt32(int i) {
        return putOctetInt(ByteUtil.INT_32_SIZE, i);
    }

    /**
     * Get single byte.
     * @deprecated
     */
    public byte getByte() {
        return get();
    }
    
    /**
     * Get multiple bytes.
     * @deprecated
     */
    public byte[] getBytes(int length) {
        byte[] dest = new byte[length];
        get(dest);
        return dest;
    }
    
    public int getInt8() {
        return get() & 0xFF;
    }
    
    public int getInt16() {
        return getOctetInt(ByteUtil.INT_16_SIZE);
    }

    public int getInt32() {
        return getOctetInt(ByteUtil.INT_32_SIZE);
    }
    
    public void putInt64(long l) {
        throw new UnsupportedOperationException();
    }

    public long getInt64() {
        throw new UnsupportedOperationException();
    }
}
