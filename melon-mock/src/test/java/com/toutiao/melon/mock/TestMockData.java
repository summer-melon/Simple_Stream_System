package com.toutiao.melon.mock;

import com.toutiao.melon.mock.data.MockData;
import org.junit.Test;

import java.util.List;

public class TestMockData {

    @Test
    public void testMockData() {
        List<String> mockData = MockData.mockData;
        for (String data : mockData) {
            assert check(data);
        }
    }

    private boolean check(String s) {
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }
}
