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

import java.util.Random;
import org.squilla.io.ByteBuffer;

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
        if (resp) {
            System.out.print(">>(" + len + ") ");
        } else {
            System.out.print("<<(" + len + ") ");
        }
        for (int i = off; i < off + len; i++) {
            System.out.print(ByteUtil.toHexString(buffer[i]) + " ");
        }
        System.out.println();
    }

    public static void printDev(ByteBuffer buffer, boolean resp) {
        printDev(buffer.array(), buffer.arrayOffset(), buffer.position(), resp);
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
}
