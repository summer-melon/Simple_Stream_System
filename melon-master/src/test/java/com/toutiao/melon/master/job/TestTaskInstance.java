package com.toutiao.melon.master.job;

import org.junit.Test;

public class TestTaskInstance {

    @Test
    public void testTaskInstanceEqual() {
        TaskInstance t1 = new TaskInstance("123", 2);
        TaskInstance t2 = new TaskInstance("123", 2);
        assert (t1.equals(t2));

        TaskInstance t3 = new TaskInstance("123", 3);
        assert (!t1.equals(t3));

        TaskInstance t4 = new TaskInstance("124", 2);
        assert (!t1.equals(t4));
    }
}
