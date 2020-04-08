package ru.hw.md5;

public class Utils {
    public static final int BLOCK_SIZE = Long.SIZE;

    public static long unsignedInt(long number) {
        return number & 0xffffffffL;
    }

    public static long rotateLeft(long value, int shift)
    {
        long unsigned = unsignedInt(value);
        return unsignedInt(unsignedInt(unsigned << shift) | unsignedInt(unsigned >>> 32 - shift));
    }
}
