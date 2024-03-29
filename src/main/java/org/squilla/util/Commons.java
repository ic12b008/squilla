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
package org.squilla.util;

import java.math.BigInteger;
import java.util.Random;
import org.squilla.io.ByteUtil;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public abstract class Commons {

    public static int countBit(int i) {
        i = i - ((i >> 1) & 0x55555555);
        i = (i & 0x33333333) + ((i >> 2) & 0x33333333);
        return ((i + (i >> 4) & 0xF0F0F0F) * 0x1010101) >> 24;
    }
    
    public static void printDev(byte[] buffer, int off, int len, boolean resp) {
        System.out.println(ByteUtil.toString(buffer, off, len, resp));
    }
    
    public static String[] split(String str, char delim) {
        char[] ca = str.toCharArray();
        int dc = 0;	//Delim Count
        for (int i = 0; i < ca.length; i++) {
            if (ca[i] == delim) {
                dc++;
            }
        }

        String[] sa = new String[dc + 1];
        // If there is no delim then return str itself.
        if (dc == 0) {
            sa[0] = str;
        } else {
            int index = 0;
            String s = "";
            for (int i = 0; i < ca.length; i++) {
                if (ca[i] != delim) {
                    s += ca[i];
                } else {
                    sa[index++] = s;
                    s = "";
                }
            }
            sa[index] = s;
        }

        return sa;
    }
    
    public static String replace(String target, String oldString, String newString) {
        int i = target.indexOf(oldString);
        if (i == -1) {
            return target;
        }
        String pre = target.substring(0, i);
        String suf = target.substring(i + oldString.length());
        return pre + newString + suf;
    }

    public static int[] randArray(int n) {
        Random rand = new Random();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }
        for (int i = (n - 1); i >= 1; i--) {
            int j = rand.nextInt(i + 1);
            if (i != j) {
                int temp = a[j];
                a[j] = a[i];
                a[i] = temp;
            }
        }
        return a;
    }

    public static int getField(int src, int offset, int length) {
        int val = src >> offset;
        int mask = ~(0xFFFFFFFF << length);
        return val & mask;
    }
    
    public static boolean parseBoolean(String name) {
        return ((name != null) && name.equalsIgnoreCase("true"));
    }
    
    public static byte parseByte(String s) {
        if (s.startsWith("0x")) {
            return Byte.parseByte(s.substring(2), 16);
        } else {
            return Byte.parseByte(s);
        }
    }
    
    public static short parseShort(String s) {
        if (s.startsWith("0x")) {
            return Short.parseShort(s.substring(2), 16);
        } else {
            return Short.parseShort(s);
        }
    }
    
    public static int parseInt(String s) {
        if (s.startsWith("0x")) {
            return Integer.parseInt(s.substring(2), 16);
        } else {
            return Integer.parseInt(s);
        }
    }
    
    public static long parseLong(String s) {
        if (s.startsWith("0x")) {
            return Long.parseLong(s.substring(2), 16);
        } else {
            return Long.parseLong(s);
        }
    }
    
    public static long parseInt64(String s) {
        if (s.startsWith("0x")) {
            return new BigInteger(s.substring(2), 16).longValue();
        } else {
            return new BigInteger(s).longValue();
        }
    }
}
