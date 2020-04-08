package ru.hw.md5;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static ru.hw.md5.Utils.*;

public class MD5 {
    private static final long[] T;

    static {
        T = LongStream
                .range(0, 64)
                .map(i -> (long)((1L << 32) * Math.abs(Math.sin(i + 1))))
                .map(Utils::unsignedInt)
                .toArray();
    }

    public static String getHash(String data) {
        val extendedData = extendData(data.getBytes());
        return rounds(extendedData);
    }

    private static byte[] extendData(byte[] data) {
        val res = ArrayUtils.addAll(data, (byte) 0x80);

        val increment = BLOCK_SIZE - Byte.SIZE;
        val padding = increment - (res.length % BLOCK_SIZE) % increment;

        return ArrayUtils.addAll(
                ArrayUtils.addAll(
                        res,
                        new byte[padding]
                ),
                // size of initial data
                ByteBuffer.allocate(Long.BYTES).order(ByteOrder.LITTLE_ENDIAN).putLong(data.length * 8).array()
        );
    }

    private static String rounds(byte[] data) {
        // IV
        long A = unsignedInt(0x67452301);
        long B = unsignedInt(0xefcdab89);
        long C = unsignedInt(0x98badcfe);
        long D = unsignedInt(0x10325476);

        val X = Lists
                .partition(Bytes.asList(data), 4)
                .parallelStream()
                .map(bytes -> unsignedInt(ByteBuffer.wrap(ArrayUtils.toPrimitive(bytes.toArray(Byte[]::new))).order(ByteOrder.LITTLE_ENDIAN).getInt()))
                .toArray(Long[]::new);

        long AA, BB, CC, DD;

        for (int i = 0; i < data.length / BLOCK_SIZE; i++) {
            // split blocks for 16 rounds
            val k = i * 16;

            AA = unsignedInt(A);
            BB = unsignedInt(B);
            CC = unsignedInt(C);
            DD = unsignedInt(D);

            A = unsignedInt(B + rotateLeft((A + F(B, C, D) + X[k] + T[0]), 7));
            D = unsignedInt(A + rotateLeft((D + F(A, B, C) + X[k + 1] + T[1]), 12));
            C = unsignedInt(D + rotateLeft((C + F(D, A, B) + X[k + 2] + T[2]), 17));
            B = unsignedInt(C + rotateLeft((B + F(C, D, A) + X[k + 3] + T[3]), 22));

            A = unsignedInt(B + rotateLeft((A + F(B, C, D) + X[k + 4] + T[4]), 7));
            D = unsignedInt(A + rotateLeft((D + F(A, B, C) + X[k + 5] + T[5]), 12));
            C = unsignedInt(D + rotateLeft((C + F(D, A, B) + X[k + 6] + T[6]), 17));
            B = unsignedInt(C + rotateLeft((B + F(C, D, A) + X[k + 7] + T[7]), 22));

            A = unsignedInt(B + rotateLeft((A + F(B, C, D) + X[k + 8] + T[8]), 7));
            D = unsignedInt(A + rotateLeft((D + F(A, B, C) + X[k + 9] + T[9]), 12));
            C = unsignedInt(D + rotateLeft((C + F(D, A, B) + X[k + 10] + T[10]), 17));
            B = unsignedInt(C + rotateLeft((B + F(C, D, A) + X[k + 11] + T[11]), 22));

            A = unsignedInt(B + rotateLeft((A + F(B, C, D) + X[k + 12] + T[12]), 7));
            D = unsignedInt(A + rotateLeft((D + F(A, B, C) + X[k + 13] + T[13]), 12));
            C = unsignedInt(D + rotateLeft((C + F(D, A, B) + X[k + 14] + T[14]), 17));
            B = unsignedInt(C + rotateLeft((B + F(C, D, A) + X[k + 15] + T[15]), 22));

            //раунд 2
            A = unsignedInt(B + rotateLeft((A + G(B, C, D) + X[k + 1] + T[16]), 5));
            D = unsignedInt(A + rotateLeft((D + G(A, B, C) + X[k + 6] + T[17]), 9));
            C = unsignedInt(D + rotateLeft((C + G(D, A, B) + X[k + 11] + T[18]), 14));
            B = unsignedInt(C + rotateLeft((B + G(C, D, A) + X[k] + T[19]), 20));

            A = unsignedInt(B + rotateLeft((A + G(B, C, D) + X[k + 5] + T[20]), 5));
            D = unsignedInt(A + rotateLeft((D + G(A, B, C) + X[k + 10] + T[21]), 9));
            C = unsignedInt(D + rotateLeft((C + G(D, A, B) + X[k + 15] + T[22]), 14));
            B = unsignedInt(C + rotateLeft((B + G(C, D, A) + X[k + 4] + T[23]), 20));

            A = unsignedInt(B + rotateLeft((A + G(B, C, D) + X[k + 9] + T[24]), 5));
            D = unsignedInt(A + rotateLeft((D + G(A, B, C) + X[k + 14] + T[25]), 9));
            C = unsignedInt(D + rotateLeft((C + G(D, A, B) + X[k + 3] + T[26]), 14));
            B = unsignedInt(C + rotateLeft((B + G(C, D, A) + X[k + 8] + T[27]), 20));

            A = unsignedInt(B + rotateLeft((A + G(B, C, D) + X[k + 13] + T[28]), 5));
            D = unsignedInt(A + rotateLeft((D + G(A, B, C) + X[k + 2] + T[29]), 9));
            C = unsignedInt(D + rotateLeft((C + G(D, A, B) + X[k + 7] + T[30]), 14));
            B = unsignedInt(C + rotateLeft((B + G(C, D, A) + X[k + 12] + T[31]), 20));

            //раунд 3
            A = unsignedInt(B + rotateLeft((A + H(B, C, D) + X[k + 5] + T[32]), 4));
            D = unsignedInt(A + rotateLeft((D + H(A, B, C) + X[k + 8] + T[33]), 11));
            C = unsignedInt(D + rotateLeft((C + H(D, A, B) + X[k + 11] + T[34]), 16));
            B = unsignedInt(C + rotateLeft((B + H(C, D, A) + X[k + 14] + T[35]), 23));

            A = unsignedInt(B + rotateLeft((A + H(B, C, D) + X[k + 1] + T[36]), 4));
            D = unsignedInt(A + rotateLeft((D + H(A, B, C) + X[k + 4] + T[37]), 11));
            C = unsignedInt(D + rotateLeft((C + H(D, A, B) + X[k + 7] + T[38]), 16));
            B = unsignedInt(C + rotateLeft((B + H(C, D, A) + X[k + 10] + T[39]), 23));

            A = unsignedInt(B + rotateLeft((A + H(B, C, D) + X[k + 13] + T[40]), 4));
            D = unsignedInt(A + rotateLeft((D + H(A, B, C) + X[k] + T[41]), 11));
            C = unsignedInt(D + rotateLeft((C + H(D, A, B) + X[k + 3] + T[42]), 16));
            B = unsignedInt(C + rotateLeft((B + H(C, D, A) + X[k + 6] + T[43]), 23));

            A = unsignedInt(B + rotateLeft((A + H(B, C, D) + X[k + 9] + T[44]), 4));
            D = unsignedInt(A + rotateLeft((D + H(A, B, C) + X[k + 12] + T[45]), 11));
            C = unsignedInt(D + rotateLeft((C + H(D, A, B) + X[k + 15] + T[46]), 16));
            B = unsignedInt(C + rotateLeft((B + H(C, D, A) + X[k + 2] + T[47]), 23));

            //раунд 4
            A = unsignedInt(B + rotateLeft((A + I(B, C, D) + X[k] + T[48]), 6));
            D = unsignedInt(A + rotateLeft((D + I(A, B, C) + X[k + 7] + T[49]), 10));
            C = unsignedInt(D + rotateLeft((C + I(D, A, B) + X[k + 14] + T[50]), 15));
            B = unsignedInt(C + rotateLeft((B + I(C, D, A) + X[k + 5] + T[51]), 21));

            A = unsignedInt(B + rotateLeft((A + I(B, C, D) + X[k + 12] + T[52]), 6));
            D = unsignedInt(A + rotateLeft((D + I(A, B, C) + X[k + 3] + T[53]), 10));
            C = unsignedInt(D + rotateLeft((C + I(D, A, B) + X[k + 10] + T[54]), 15));
            B = unsignedInt(C + rotateLeft((B + I(C, D, A) + X[k + 1] + T[55]), 21));

            A = unsignedInt(B + rotateLeft((A + I(B, C, D) + X[k + 8] + T[56]), 6));
            D = unsignedInt(A + rotateLeft((D + I(A, B, C) + X[k + 15] + T[57]), 10));
            C = unsignedInt(D + rotateLeft((C + I(D, A, B) + X[k + 6] + T[58]), 15));
            B = unsignedInt(C + rotateLeft((B + I(C, D, A) + X[k + 13] + T[59]), 21));

            A = unsignedInt(B + rotateLeft((A + I(B, C, D) + X[k + 4] + T[60]), 6));
            D = unsignedInt(A + rotateLeft((D + I(A, B, C) + X[k + 11] + T[61]), 10));
            C = unsignedInt(D + rotateLeft((C + I(D, A, B) + X[k + 2] + T[62]), 15));
            B = unsignedInt(C + rotateLeft((B + I(C, D, A) + X[k + 9] + T[63]), 21));

            A = unsignedInt(A + AA);
            B = unsignedInt(B + BB);
            C = unsignedInt(C + CC);
            D = unsignedInt(D + DD);
        }

        return Arrays.stream(new long[] {A, B, C, D}).parallel().mapToObj(Long::toHexString)
                .map(hex -> StringUtils.leftPad(hex, hex.length() + (8 - hex.length() % 8) % 8, '0'))
                .map(hex -> Splitter.fixedLength(2).split(hex))
                .map(iter -> Iterables.toArray(iter, String.class))
                .peek(ArrayUtils::reverse)
                .flatMap(Arrays::stream)
                .map(String::toUpperCase)
                .collect(Collectors.joining());
    }

    private static long F(long X, long Y, long Z)
    {
        long unsignedX = unsignedInt(X);
        long unsignedY = unsignedInt(Y);
        long unsignedZ = unsignedInt(Z);

        return unsignedInt((unsignedInt(unsignedX & unsignedY) | unsignedInt((~unsignedX) & unsignedZ)));
    }

    private static long G(long X, long Y, long Z)
    {
        long unsignedX = unsignedInt(X);
        long unsignedY = unsignedInt(Y);
        long unsignedZ = unsignedInt(Z);

        return unsignedInt(unsignedInt(unsignedX & unsignedZ) | unsignedInt(unsignedY & (~unsignedZ)));
    }

    private static long H(long X, long Y, long Z)
    {
        long unsignedX = unsignedInt(X);
        long unsignedY = unsignedInt(Y);
        long unsignedZ = unsignedInt(Z);

        return unsignedInt((unsignedX ^ unsignedY ^ unsignedZ));
    }

    private static long I(long X, long Y, long Z)
    {
        long unsignedX = unsignedInt(X);
        long unsignedY = unsignedInt(Y);
        long unsignedZ = unsignedInt(Z);

        return unsignedInt(unsignedY ^ unsignedInt(unsignedX | (~unsignedZ)));
    }
}
