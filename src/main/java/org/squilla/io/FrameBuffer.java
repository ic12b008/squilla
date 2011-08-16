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

import org.squilla.util.ByteUtil;

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
        return putOctetInt(octet, position(), value);
    }
    
    public FrameBuffer putOctetInt(int octet, int index, int value) {
        if (readOnly) {
            throw new ReadOnlyBufferException();
        }
        ByteUtil.getByteUtil(order()).toByteArray(value, octet, array, offset + index);
        return this;
    }

    public char getChar() {
        return (char) getOctetInt(ByteUtil.INT_16_SIZE);
    }

    public ByteBuffer putChar(char value) {
        return putOctetInt(ByteUtil.INT_16_SIZE, value);
    }

    public char getChar(int index) {
        return (char) getOctetInt(ByteUtil.INT_16_SIZE, index);
    }

    public ByteBuffer putChar(int index, char value) {
        return putOctetInt(ByteUtil.INT_16_SIZE, index, value);
    }

    public short getShort() {
        return (short) getOctetInt(ByteUtil.INT_16_SIZE);
    }

    public ByteBuffer putShort(short value) {
        return putOctetInt(ByteUtil.INT_16_SIZE, value);
    }

    public short getShort(int index) {
        return (short) getOctetInt(ByteUtil.INT_16_SIZE, index);
    }

    public ByteBuffer putShort(int index, short value) {
        return putOctetInt(ByteUtil.INT_16_SIZE, index, value);
    }

    public int getInt() {
        return getOctetInt(ByteUtil.INT_32_SIZE);
    }

    public ByteBuffer putInt(int value) {
        return putOctetInt(ByteUtil.INT_32_SIZE, value);
    }

    public int getInt(int index) {
        return getOctetInt(ByteUtil.INT_32_SIZE, index);
    }

    public ByteBuffer putInt(int index, int value) {
        return putOctetInt(ByteUtil.INT_32_SIZE, index, value);
    }

    public long getLong() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ByteBuffer putLong(long value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getLong(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ByteBuffer putLong(int index, long value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getFloat() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ByteBuffer putFloat(float value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getFloat(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ByteBuffer putFloat(int index, float value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getDouble() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ByteBuffer putDouble(double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getDouble(int index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ByteBuffer putDouble(int index, double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}
