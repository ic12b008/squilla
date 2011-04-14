/*
 *  Copyright 2011 Shotaro Uchida <fantom@xmaker.mx>.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  under the License.
 */

package org.squilla.service;

import java.util.List;
import java.util.Vector;
import org.squilla.util.ArrayFifoQueue;
import org.squilla.util.FifoQueue;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public abstract class AbstractProcessor implements Processor {

    private FifoQueue queue;
    private Thread processThread = null;
    private List listenerList;
    private boolean nonBlockingFire;

    public AbstractProcessor(int queueSize) {
        queue = new ArrayFifoQueue(queueSize);
        listenerList = new Vector();
    }

    public void process(Object o) {
        queue.enqueue(o);
    }

    public void addListener(ProcessListener listener) {
        listenerList.add(listener);
    }

    public boolean activate() {
        if (processThread != null) {
            return false;
        }

        processThread = new ProcessThread();
        processThread.start();
        return true;
    }

    public abstract Object processNext(Object o) throws Exception;

    private void fireDone(final Object o, final Object result, boolean block) {
        for (int i = 0; i < listenerList.size(); i++) {
            final ProcessListener listener = (ProcessListener) listenerList.get(i);
            Runnable r = new Runnable() {
                public void run() {
                    listener.done(o, result);
                }
            };
            if (!block) {
                new Thread(r).start();
            } else {
                r.run();
            }
        }
    }

    private void fireFailed(final Object o, final Object reason, boolean nonBlock) {
        for (int i = 0; i < listenerList.size(); i++) {
            final ProcessListener listener = (ProcessListener) listenerList.get(i);
            Runnable r = new Runnable() {
                public void run() {
                    listener.failed(o, reason);
                }
            };
            if (nonBlock) {
                new Thread(r).start();
            } else {
                r.run();
            }
        }
    }

    /**
     * @return the nonBlockingFire
     */
    public boolean isNonBlockingFire() {
        return nonBlockingFire;
    }

    /**
     * @param nonBlockingFire the nonBlockingFire to set
     */
    public void setNonBlockingFire(boolean nonBlockingFire) {
        this.nonBlockingFire = nonBlockingFire;
    }

    private class ProcessThread extends Thread {

        public void run() {
            while (true) {
                Object o = queue.blockingDequeue();
                try {
                    Object result = processNext(o);
                    fireDone(o, result, isNonBlockingFire());
                } catch (Exception ex) {
                    fireFailed(o, ex, isNonBlockingFire());
                }
            }
        }
    }
}
