package com.toutiao.melon.mock.faker;

import com.toutiao.melon.mock.data.MockData;
import com.toutiao.melon.mock.entiry.Event;

import java.util.List;

public class StatelessBigFaker {

    /** 使用单词数量 */
    private int maxWords;
    /** 最大可能次数 */
    private int maxCount;

    private static List<String> mockData;

    static {
        mockData = MockData.mockData;
    }

    public StatelessBigFaker() {
    }

    public StatelessBigFaker(int maxWords, int maxCount) {
        this.maxWords = Math.min(maxWords, mockData.size());
        this.maxCount = maxCount;
    }

    private static int rand(int max) {
        return (int) (Math.random() * max);
    }

    private static String generateNewWord(String word) {
        char[] chars = word.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            double random = Math.random();
            if (random >= 0.5) {
                chars[i] = (char) (chars[i] - 'a' + 'A');
            }
        }
        return new String(chars);
    }

    public Event mock() {
        int wordIdx = StatelessBigFaker.rand(maxWords);
        String updateWord = StatelessBigFaker.generateNewWord(mockData.get(wordIdx));
        int wordCount = StatelessBigFaker.rand(maxCount) + 1;
        return new Event(updateWord, wordCount);
    }
}
