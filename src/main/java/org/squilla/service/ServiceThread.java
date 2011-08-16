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

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public abstract class ServiceThread extends Thread {

    private boolean active = false;
    private volatile boolean shutdownRequested;
    private final Object activeLock = new Object();

    public void activate() {
        synchronized (activeLock) {
            if (active) {
                return;
            }
            active = true;
            shutdownRequested = false;
        }
        this.start();
    }

    public void shutdown() {
        shutdownRequested = true;
    }
    
    public boolean isActive() {
        synchronized (activeLock) {
            return active;
        }
    }
    
    public boolean shutdownAndWait(int timeout) {
        shutdown();
        return waitForShutdown(timeout);
    }

    public boolean waitForShutdown(int timeout) {
        synchronized (activeLock) {
            if (active) {
                try {
                    activeLock.wait(timeout);
                } catch (InterruptedException ex) {
                }
            }
            return !active;
        }
    }

    public final void run() {
        while (!shutdownRequested) {
            try {
                taskLoop();
            } catch (Exception ex) {
                System.err.println("[ServiceThread] Uncaught exception: " + ex);
                ex.printStackTrace();
            } catch (Throwable t) {
                System.err.println("[ServiceThread] Uncaught error: " + t);
                t.printStackTrace();
            }
        }
        
        synchronized (activeLock) {
            active = false;
            activeLock.notifyAll();
        }
    }

    protected abstract void taskLoop();
}
