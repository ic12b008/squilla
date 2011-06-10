/**
 * Copyright (c) 2009 - 2011, Valley Campus Japan, Inc.
 * All Rights Reserved.
 */
package org.squilla.i2c.eeprom;

import java.io.IOException;
import org.squilla.i2c.I2CMaster;

public abstract class MicrochipEEPROM {

    public static final int MAX_ADDRESS = 0x7;
    private static final int BASE_ADDRESS = 0x50;
    private static final int ADDRESS_MASK = 0xF0;
    private static final long DEFAULT_TIMEOUT = 20000L;
    private I2CMaster i2c;
    private final int maxBytes;
    private final int pageSize;
    private final int addressSize;
    private int slaveAddress;
    private byte[] writeBuffer;
    private long timeout;

    public MicrochipEEPROM(I2CMaster i2c, int maxBytes, int pageSize, int addressSize) {
        this.i2c = i2c;
        this.maxBytes = maxBytes;
        this.pageSize = pageSize;
        this.addressSize = addressSize;
        writeBuffer = new byte[pageSize + addressSize];
        setTimeout(DEFAULT_TIMEOUT);
    }

    public void setSlaveAddress(int addr) throws Exception {
        if (addr < 0 || MAX_ADDRESS < addr) {
            throw new Exception("[EEPROM_24LC256] Chip select out of Range");
        }
        this.slaveAddress = BASE_ADDRESS | addr;
        return;
    }

    public int getStorageSize() {
        return maxBytes;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getAddressSize() {
        return addressSize;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int read(byte[] data) throws IOException {
        setLocation(0);
        int length = (data.length >= maxBytes ? maxBytes : data.length);
        i2c.write(slaveAddress, writeBuffer, 0, addressSize, false);
        i2c.read(slaveAddress, data, 0, length);
        return length;
    }

    public byte read(int loc) throws IOException {
        if (loc < 0 || loc >= maxBytes) {
            throw new IOException("[EEPROM_24LC256] Index out of Range");
        }
        setLocation(loc);
        i2c.write(slaveAddress, writeBuffer, 0, addressSize, false);
        i2c.read(slaveAddress, writeBuffer, 0, 1);
        return writeBuffer[0];
    }

    public int read(int offset, int len, byte[] data, int loc) throws IOException {
        setLocation(loc);
        int length = ((loc + len) <= maxBytes ? len : (maxBytes - loc));
        i2c.write(slaveAddress, writeBuffer, 0, addressSize, false);
        i2c.read(slaveAddress, data, offset, length);
        return length;
    }

    public void write(byte[] data) throws IOException {
        setLocation(0);
        int length = (data.length >= maxBytes ? maxBytes : data.length);
        int size = (length >= pageSize ? pageSize : length);

//        int srcPointer = rawJEM.toInt(data) + OBJECT.ARRAY_ELEMENT0;
//        int destPointer = rawJEM.toInt(writeBuffer) + OBJECT.ARRAY_ELEMENT0 + addressSize;
//        rawJEM.bblkcpy(srcPointer, destPointer, size);
        int srcIndex = 0;
        System.arraycopy(data, srcIndex, writeBuffer, addressSize, size);
        i2c.write(slaveAddress, writeBuffer, 0, size + addressSize, true);
        while ((length = length - size) != 0) {
            int nextLoc = getLocation() + size;
            setLocation(nextLoc);
            size = (length >= pageSize ? pageSize : length);
//            srcPointer += size;
//            rawJEM.bblkcpy(srcPointer, destPointer, size);
            srcIndex += size;
            System.arraycopy(data, srcIndex, writeBuffer, addressSize, size);
            i2c.write(slaveAddress, writeBuffer, 0, size + addressSize, true);
        }
        return;
    }

    public void write(byte value, int loc) throws IOException {
        if (loc < 0 || loc >= maxBytes) {
            throw new IOException("[EEPROM_24LC256] Index out of Range");
        }
        setLocation(loc);
        writeBuffer[addressSize] = value;
        i2c.write(slaveAddress, writeBuffer, 0, 1 + addressSize, true);
    }

    public void write(int offset, int len, byte[] data, int loc) throws IOException {
        setLocation(loc);
        int length = ((loc + len) <= maxBytes ? len : maxBytes - loc);
        int strtPage = loc & ADDRESS_MASK;
        int endPage = (loc + length - 1) & ADDRESS_MASK;
        int size = (strtPage != endPage ? pageSize - loc - strtPage : length);

//        int srcPointer = rawJEM.toInt(data) + OBJECT.ARRAY_ELEMENT0 + offset;
//        int destPointer = rawJEM.toInt(writeBuffer) + OBJECT.ARRAY_ELEMENT0 + addressSize;
//        rawJEM.bblkcpy(srcPointer, destPointer, size);
        int srcIndex = offset;
        System.arraycopy(data, srcIndex, writeBuffer, addressSize, size);
        i2c.write(slaveAddress, writeBuffer, 0, size + addressSize, true);
        while ((length = length - size) != 0) {
            int nextLoc = getLocation() + size;
            setLocation(nextLoc);
            size = (length >= pageSize ? pageSize : length);
//            srcPointer += size;
//            rawJEM.bblkcpy(srcPointer, destPointer, size);
            srcIndex += size;
            System.arraycopy(data, srcIndex, writeBuffer, addressSize, size);
            
            i2c.write(slaveAddress, writeBuffer, 0, size + addressSize, true);
        }
        return;
    }

//    private void writeAsync(int address, byte[] data, int offset, int bytesToWrite, boolean stopCondition) throws IOException {
//        long t = rawJEM.getTime() + getTimeout();
//        while (true) {
//            try {
//                i2c.write(slaveAddress, data, offset, bytesToWrite, stopCondition);
//                break;
//            } catch (IOException ex) {
//                if ((rawJEM.getTime() - t) > 0) {
//                    throw ex;
//                }
//                continue;
//            }
//        }
//    }

    private void setLocation(int loc) {
        switch (addressSize) {
        case 1:
            writeBuffer[0] = (byte) (loc & 0xFF);
            break;
        case 2:
            writeBuffer[0] = (byte) ((loc >> 8) & 0xFF);
            writeBuffer[1] = (byte) (loc & 0xFF);
        }
    }

    private int getLocation() {
        switch (addressSize) {
        case 1:
            return writeBuffer[0];
        case 2:
            int loc = 0;
            loc |= (writeBuffer[0] << 8) & 0xFF00;
            loc |= writeBuffer[1] & 0xFF;
            return loc;
        }
        return -1;
    }
}
