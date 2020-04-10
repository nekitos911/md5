package ru.hw.md5.benchmark;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import ru.hw.md5.MD5;
import ru.hw.md5.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;

public class Benchmark {
    private static int ITERATIONS = 10;
    private static int BLOCKS = 1_000_000;
    private static final String FOLDER = "benchmarks/";
    private static final String FILE = FOLDER + "benchmark.txt";

    public static void main(String[] args) throws IOException {
        val res = testHash();

        if (!Files.exists(Paths.get(FOLDER))) {
            Files.createDirectory(Paths.get(FOLDER));
        }

        Files.write(Paths.get(FILE), ("=".repeat(50) + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Files.write(Paths.get(FILE), (LocalDateTime.now().toString() + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Files.write(Paths.get(FILE), ("iterations: " + ITERATIONS + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Files.write(Paths.get(FILE), ("blocks count: " + BLOCKS + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Files.write(Paths.get(FILE), (res + "ms" + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Files.write(Paths.get(FILE), ("=".repeat(50) + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    @SneakyThrows
    private static long testHash() {
        val data = RandomStringUtils.randomAscii(BLOCKS * Utils.BLOCK_SIZE).getBytes();

        // warmup
        for (int i = 0; i < 10; i++) {
            System.out.println(MD5.getHash(data));
        }

        String res = null;
        var begin = Instant.now();
        for (int i = 0; i < ITERATIONS; i++) {
            res = MD5.getHash(data);
        }
        val duration = Duration.between(begin, Instant.now()).abs().toMillis() / ITERATIONS;
        System.out.println(res);
        return duration;
    }
}
