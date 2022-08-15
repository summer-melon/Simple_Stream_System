package com.toutiao.melon.mock.entiry;

public class Event {

    private String word;

    private int count;

    public Event() {
    }

    public Event(String word, int count) {
        this.word = word;
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Event{"
                + "word='" + word + '\''
                + ", count=" + count
                + '}';
    }
}
