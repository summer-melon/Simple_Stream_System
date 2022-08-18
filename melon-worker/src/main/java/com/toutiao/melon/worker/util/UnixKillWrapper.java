

package com.toutiao.melon.worker.util;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface UnixKillWrapper extends Library {

    UnixKillWrapper INSTANCE = Native.load("c", UnixKillWrapper.class);

    int kill(int pid, int sig);
}
