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
package org.squilla.service;

import org.squilla.util.ArrayFifoQueue;

/**
 * Tweaked version of IBM's WorkQueue example.
 * 
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class WorkQueue implements Service {

    private final int poolSize;
    private final int queueSize;
    private final PoolWorker[] threads;
    private final ArrayFifoQueue queue;
    private boolean active = false;

    public WorkQueue(int poolSize, int queueSize) {
        this.poolSize = poolSize;
        this.queueSize = queueSize;
        queue = new ArrayFifoQueue(queueSize);
        threads = new PoolWorker[poolSize];
    }
    
    public int getQueueSize() {
        return queueSize;
    }
    
    public int remainingTask() {
        return queue.size();
    }

    public synchronized boolean activate() {
        if (active) {
            return false;
        }
        for (int i = 0; i < poolSize; i++) {
            threads[i] = new PoolWorker();
            threads[i].activate();
        }
        active = true;
        return true;
    }
    
    public synchronized boolean shutdown() {
        if (!active) {
            return false;
        }
        for (int i = 0; i < poolSize; i++) {
            threads[i].shutdown();
        }
        active = false;
        return true;
    }
    
    public void execute(Runnable r) {
        queue.enqueue(r);
    }

    private class PoolWorker extends ServiceTask {

        protected void taskLoop() {
            Runnable r = (Runnable) queue.blockingDequeue();
            try {
                r.run();
            } catch (Throwable t) {
                t.printStackTrace();
                System.out.println("[PoolWorker] Uncaught Exception!");
            }
        }
    }
}