package ru.hw.md5;

import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMD5 {

    @ParameterizedTest
    @CsvSource
            (value = {"md5:1BC29B36F623BA82AAF6724FD3B16718",
                    "md4:C93D3BF7A7C4AFE94B64E30C2CE39F4F",
                    "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest:9C3EE9CFC67011D825B5046C1BCD479B"},
                    delimiter = ':')
    public void test(String inputStr, String expectedHash) {
        val actualHash = MD5.getHash(inputStr.getBytes());
        assertEquals(expectedHash, actualHash);
    }
}
