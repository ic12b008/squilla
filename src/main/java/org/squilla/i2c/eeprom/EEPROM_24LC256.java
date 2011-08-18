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
package org.squilla.i2c.eeprom;

import org.squilla.i2c.I2CMaster;

/**
 *
 * @author Shotaro Uchida <fantom@xmaker.mx>
 */
public class EEPROM_24LC256 extends MicrochipEEPROM {

    private static final int ADDR_SIZE = 2;
    private static final int PAGE_SIZE = 64;
    private static final int MAX_BYTES = 32768;

    public EEPROM_24LC256(I2CMaster i2c) {
        super(i2c, MAX_BYTES, PAGE_SIZE, ADDR_SIZE);
    }
}
