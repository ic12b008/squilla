/**
 * Copyright (c) 2009 - 2011, Valley Campus Japan, Inc.
 * All Rights Reserved.
 */
package org.squilla.i2c.eeprom;

import org.squilla.i2c.I2CMaster;

public class EEPROM_24LC256 extends MicrochipEEPROM {

    private static final int ADDR_SIZE = 2;
    private static final int PAGE_SIZE = 64;
    private static final int MAX_BYTES = 32768;

    public EEPROM_24LC256(I2CMaster i2c) {
        super(i2c, MAX_BYTES, PAGE_SIZE, ADDR_SIZE);
    }
}
