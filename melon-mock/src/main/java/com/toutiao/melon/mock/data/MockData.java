package com.toutiao.melon.mock.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MockData {

    private static final String FILE_NAME = "mockData.txt";
    public static List<String> mockData = new ArrayList<>();

    static {
        String filePath = Objects.requireNonNull(
                MockData.class.getClassLoader().getResource(MockData.FILE_NAME)).getFile();

        try (FileReader reader = new FileReader(filePath);
             BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                mockData.add(line.toLowerCase(Locale.ROOT));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
