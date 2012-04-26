/*
 * Copyright 2012 Shotaro Uchida <suchida@valleycampus.com>.
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
package org.squilla.service;

import java.util.ArrayList;
import java.util.List;
import org.squilla.util.ArrayFifoQueue;
import org.squilla.util.BlockingFifoQueue;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public abstract class AbstractProcessor implements Processor {

    private BlockingFifoQueue queue;
    private ServiceTask processTask = null;
    private List listenerList;
    private boolean nonBlockingFire;

    public AbstractProcessor(int queueSize) {
        queue = new ArrayFifoQueue(queueSize);
        listenerList = new ArrayList();
        processTask = new ProcessTask();
    }

    public void process(Object o) {
        queue.blockingEnqueue(o);
    }

    public void addListener(ProcessListener listener) {
        listenerList.add(listener);
    }

    public synchronized boolean activate() {
        return processTask.activate();
    }
    
    public synchronized boolean shutdown() {
        return processTask.shutdown();
    }

    protected abstract Object processNext(Object o) throws Exception;
    
    protected BlockingFifoQueue getRawQueue() {
        return queue;
    }

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

    private class ProcessTask extends ServiceTask {

        public void taskLoop() {
            Object o = queue.blockingDequeue();
            if (o == null) {
                return;
            }
            try {
                Object result = processNext(o);
                fireDone(o, result, isNonBlockingFire());
            } catch (Exception ex) {
                fireFailed(o, ex, isNonBlockingFire());
            }
        }
    }
}
