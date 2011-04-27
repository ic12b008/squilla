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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class BufferedPipe {

    private static final int DEFAULT_TIMEOUT = 100;
    private byte[] buffer;
    private int bufferSize;
    private int head;
    private int tail;
    private int timeout;
    private int incoming;
    private boolean bBlockingRead = true;
    private final Object pipeLock;
    private final PipeInputStream pis;
    private final PipeOutputStream pos;
    private boolean shutdown = false;

    public BufferedPipe(int bufferSize) {
        this.bufferSize = bufferSize;
        buffer = new byte[bufferSize];
        head = 0;
        tail = 0;
        timeout = DEFAULT_TIMEOUT;
        pipeLock = new Object();
        pis = new PipeInputStream();
        pos = new PipeOutputStream();
    }

    public boolean isBlockingRead() {
        return bBlockingRead;
    }

    public void setBlockingRead(boolean b) {
        this.bBlockingRead = b;
    }
    
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaximum() {
        return bufferSize - 1;
    }

    public boolean isEmpty() {
        synchronized (pipeLock) {
            return head == tail;
        }
    }

    public boolean isFull() {
        synchronized (pipeLock) {
            return ((head + 1) % bufferSize) == tail;
        }
    }
    
    public void shutdown(boolean shutdown) {
        synchronized (pipeLock) {
            this.shutdown = shutdown;
            pipeLock.notifyAll();
        }
    }

    protected int bufferAvailable() {
        if (isEmpty()) {
            return 0;
        } else {
            synchronized (pipeLock) {
                if (head > tail) {
                    return head - tail;
                } else {
                    return (bufferSize - tail) + head;
                }
            }
        }
    }

    public InputStream getInputStream() {
        return pis;
    }

    public OutputStream getOutputStream() {
        return pos;
    }

    private class PipeInputStream extends InputStream {

        public int available() {
            return bufferAvailable();
        }

        public int read() {
            if (isEmpty()) {
                waitMinIncoming();
                synchronized (pipeLock) {
                    if (shutdown) {
                        return -1;
                    }
                }
            }

            int b;
            synchronized (pipeLock) {
                b = buffer[tail];
                tail = (tail + 1) % bufferSize;
            }
            return b & 0xFF;
        }

        public int read(byte[] b) {
            return read(b, 0, available());
        }

        public int read(byte[] b, int off, int length) {
            if (length > (b.length - off)) {
                throw new IndexOutOfBoundsException();
            }

            if (length > available()) {
                synchronized (pipeLock) {
                    if (shutdown) {
                        return -1;
                    }
                }
                if (bBlockingRead) {
                    if (!waitIncoming(length)) {
                        return 0;
                    }
                } else {
                    length = available();
                }
            }

            synchronized (pipeLock) {
                if (head > tail) {
                    copy(buffer, tail, b, off, length);
                } else {
                    int front = bufferSize - tail;
                    if (length <= front) {
                        copy(buffer, tail, b, off, length);
                    } else {
                        copy(buffer, tail, b, off, front);
                        copy(buffer, 0, b, off + front, length - front);
                    }
                }
                tail = (tail + length) % bufferSize;
            }
            return length;
        }

        public long skip(long n) {
            if (isEmpty()) {
                return 0;
            } else {
                int available = available();
                if (n > available) {
                    n = available;
                }
                synchronized (pipeLock) {
                    tail = (tail + (int) n) % bufferSize;
                }
                return n;
            }
        }
        
        private void waitMinIncoming() {
            synchronized (pipeLock) {
                incoming = 1;
                while (available() == 0 && !shutdown) {
                    try {
                        pipeLock.wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }

        private boolean waitIncoming(int length) {
            synchronized (pipeLock) {
                incoming = length;
                try {
                    pipeLock.wait(timeout);
                } catch (InterruptedException ex) {
                }
                if (incoming <= available()) {
                    return true;
                }
                return false;
            }
        }
    }

    private class PipeOutputStream extends OutputStream {

        public void write(int b) throws IOException {
            checkIsShutdownRequested();
            if (!isFull()) {
                synchronized (pipeLock) {
                    buffer[head] = (byte) (b & 0xFF);
                    head = (head + 1) % bufferSize;
                    if (bBlockingRead) {
                        notifyIncoming();
                    }
                }
            }
        }

        public void write(byte[] b) throws IOException {
            write(b, 0, b.length);
        }

        public void write(byte[] b, int off, int length) throws IOException {
            checkIsShutdownRequested();
            int space = getMaximum() - bufferAvailable();
            if (length > space) {
                length = space;
            }

            int woff = 0;
            int slot = length;
            while (!isFull()) {
                synchronized (pipeLock) {
                    int count;
                    if (head >= tail) {
                        count = bufferSize - head;
                    } else {
                        count = tail;
                    }
                    if (slot > count) {
                        slot = count;
                    }
                    copy(b, off + woff, buffer, head, slot);
                    head = (head + slot) % bufferSize;
                    woff += slot;
                    slot = length - slot;
                    if ((woff == length) && bBlockingRead) {
                        notifyIncoming();
                        break;
                    }
                }
            }
        }
        
        public void flush() throws IOException {
            checkIsShutdownRequested();
            // TODO
        }
        
        private void checkIsShutdownRequested() throws IOException {
            synchronized (pipeLock) {
                if (shutdown) {
                    throw new IOException("Shutdown requested!");
                }
            }
        }

        private void notifyIncoming() {
            synchronized (pipeLock) {
                if (incoming <= bufferAvailable()) {
                    pipeLock.notify();
                }
            }
        }
    }

    private static void copy(byte[] src, int srcPos, byte[] dest, int destPos, int length) {
//        rawJEM.bblkcpy(
//                rawJEM.toInt(src) + OBJECT.ARRAY_ELEMENT0 + srcPos,
//                rawJEM.toInt(dest) + OBJECT.ARRAY_ELEMENT0 + destPos,
//                length);
        System.arraycopy(src, srcPos, dest, destPos, length);
    }
}
