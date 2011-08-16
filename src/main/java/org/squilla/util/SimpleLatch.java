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
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class SimpleLatch implements Latch {
    
    private Object obj;
    private boolean awaiting = false;
    
    public synchronized boolean isAwaiting() {
        return awaiting;
    }

    public Object await() {
        return await(0);
    }

    public synchronized Object await(int timeout) {
        while (obj == null) {
            awaiting = true;
            try {
                wait(timeout);
            } catch (InterruptedException ex) {
            }
            if (timeout != 0 && (obj == null)) {
                awaiting = false;
                return null;
            }
        }
        Object e = get();
        awaiting = false;
        return e;
    }
    
    public synchronized Object get() {
        Object e = obj;
        obj = null;
        return e;
    }

    public synchronized boolean set(Object e) {
        if (obj != null) {
            return false;
        }
        if (e == null) {
            throw new NullPointerException();
        }
        obj = e;
        notifyAll();
        return true;
    }
}
