package com.toutiao.melon.mock.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MockData {

    private static final String FILE_NAME = "mockData.txt";
    public static List<String> mockData = new ArrayList<>();

    static {
        try (InputStream in = MockData.class.getResourceAsStream("/mockData.txt");
             InputStreamReader isr = new InputStreamReader(in);
             BufferedReader br = new BufferedReader(isr);
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
