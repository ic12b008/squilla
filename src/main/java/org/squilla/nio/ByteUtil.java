package org.squilla.nio;

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

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class ByteUtil {
    
    public static final ByteUtil LITTLE_ENDIAN = new ByteUtil(ByteOrder.LITTLE_ENDIAN);
    public static final ByteUtil BIG_ENDIAN = new ByteUtil(ByteOrder.BIG_ENDIAN);
    
    public static final int INT_64_SIZE = 8;
    public static final int INT_32_SIZE = 4;
    public static final int INT_16_SIZE = 2;
    public static final int INT_8_SIZE = 1;
    public static final int BYTE_SIZE = 8;
    
    private final ByteOrder byteOrder;
    
    private ByteUtil(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }
    
    public static ByteUtil getByteUtil(ByteOrder bo) {
        if (bo == ByteOrder.LITTLE_ENDIAN) {
            return LITTLE_ENDIAN;
        } else {
            return BIG_ENDIAN;
        }
    }
    
    public static int hashcode(byte[] a) {
        return hashcode(a, 0, a.length);
    }
    
    public static int hashcode(byte[] a, int offset, int length) {
        if (a == null) {
            return 0;
        }

        int hash = 1;
        for (int i = offset; i < offset + length; i++) {
            hash = 31 * hash + a[i];
        }
        
        return hash;
    }
    
    public static String toHexString(byte b) {
        String hex = Integer.toHexString(b);
        int len = hex.length();
        if (len == 2) {
            return hex;
        } else if (len == 1) {
            return "0" + hex;
        } else {
            return hex.substring(len - 2);
        }
    }

    public static String toHexString(byte[] data, int offset, int length) {
        String s = "";
        for (int i = offset; i < offset + length; i++) {
            s += toHexString(data[i]);
        }
        return s;
    }
    
    public int toInt(byte[] src, int off, int octet) {
        if (octet > INT_32_SIZE) {
            throw new IllegalArgumentException("Max 4 octet for Int");
        }
        return (int) (toLong(src, off, octet) & 0xFFFFFFFFL);
    }
    
    public int toInt16(byte[] src, int off) {
        return toInt(src, off, INT_16_SIZE);
    }
    
    public int toInt32(byte[] src, int off) {
        return toInt(src, off, INT_32_SIZE);
    }
    
    public long toLong(byte[] src, int off, int octet) {
        if (octet > INT_64_SIZE) {
            throw new IllegalArgumentException("Max 8 octet for Long");
        }
        long dest = 0;
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            for (int p = 0; p < octet; p++) {
                long d = src[off + p] & 0xff;
                dest |= (d << (BYTE_SIZE * p));
            }
        } else {
            for (int p = 0; p < octet; p++) {
                long d = src[off + p] & 0xff;
                dest |= (d << (BYTE_SIZE * (octet - 1 - p)));
            } 
        }
        return dest;
    }
    
    public long toInt64(byte[] src, int off) {
        return toLong(src, off, INT_64_SIZE);
    }

    public int[] toInt32Array(byte[] src, int off, int size, int length) {
        int[] dest = new int[length];
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            for (int index = 0; index < length; index++) {
                dest[index] = 0;
                for (int p = 0; p < size; p++) {
                    int d = src[off + (p + size * index)] & 0xff;
                    dest[index] |= (d << (BYTE_SIZE * p));
                }
            }
        } else {
            for (int index = 0; index < length; index++) {
                dest[index] = 0;
                for (int p = 0; p < size; p++) {
                    int d = src[off + (p + size * index)] & 0xff;
                    dest[index] |= (d << (BYTE_SIZE * (size - 1 - p)));
                }
            }  
        }
        return dest;
    }
    
    public byte[] toByteArray(int[] src, int size, int length) {
        byte[] dest = new byte[size * length];
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            for (int index = 0; index < length; index++) {
                for (int p = 0; p < size; p++) {
                    dest[p + size * index] = (byte) ((src[index] >> (BYTE_SIZE * p)) & 0xff);
                }
            }
        } else {
            for (int index = 0; index < length; index++) {
                for (int p = 0; p < size; p++) {
                    dest[p + size * index] = (byte) ((src[index] >> (BYTE_SIZE * (size - 1 - p))) & 0xff);
                }
            }
        }
        return dest;
    }
    
    public byte[] toByteArray(long src, int size) {
        byte[] dest = new byte[size];
        toByteArray(src, size, dest, 0);
        return dest;
    }

    public void toByteArray(int src, int size, byte[] dest, int destOff) {
        toByteArray(src & 0xFFFFFFFFL, size, dest, destOff);
    }
    
    public void toByteArray(long src, int size, byte[] dest, int destOff) {
        if (byteOrder == ByteOrder.LITTLE_ENDIAN) {
            for (int p = 0; p < size; p++) {
                dest[destOff + p] = (byte) ((src >> (BYTE_SIZE * p)) & 0xff);
            }
        } else {
            for (int p = 0; p < size; p++) {
                dest[destOff + p] = (byte) ((src >> (BYTE_SIZE * (size - 1 - p))) & 0xff);
            } 
        }
    }
    
    public byte[] toByteArray(String src) {
        if ((src.length() % 2) != 0) {
            src = "0" + src;
        }
        byte[] a = src.getBytes();
        byte[] dest = new byte[src.length() / 2];
        
        int destP;
        if (byteOrder == ByteOrder.BIG_ENDIAN) {
            destP = 0;
        } else {
            destP = dest.length - 1;
        }
        
        for (int i = 0; i < src.length(); i += 2) {
            byte b = (byte) Integer.parseInt(new String(a, i, 2), 16);
            if (byteOrder == ByteOrder.BIG_ENDIAN) {
                dest[destP++] = b;
            } else {
                dest[destP--] = b;
            }
        }
        
        return dest;
    }
}
