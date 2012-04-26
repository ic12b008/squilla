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

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public abstract class ServiceTask implements Service, Runnable {

    private volatile boolean shutdownRequested = false;
    private volatile Thread context = null;
    private final String contextName;
    private int contextPriority = -1;
    
    public ServiceTask(String name, int priority) {
        this.contextName = name;
        this.contextPriority = priority;
    }
    
    public ServiceTask(String name) {
        this(null, Thread.NORM_PRIORITY);
    }
    
    public ServiceTask() {
        this(null);
    }
    
    protected Thread getContext() {
        return context;
    }

    public boolean activate() {
        if (context != null) {
            return false;
        }
        if (contextName != null) {
            context = new Thread(this, contextName);
        } else {
            context = new Thread(this);
        }
        if (contextPriority != -1) {
            context.setPriority(contextPriority);
        }
        context.start();
        return true;
    }

    public boolean shutdown() {
        shutdownRequested = true;
        context.interrupt();
        return true;
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
    }

    protected abstract void taskLoop();
}
