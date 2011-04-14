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

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public interface Processor {

    public void process(Object o);

    public void addListener(ProcessListener listener);

    public boolean activate();
}