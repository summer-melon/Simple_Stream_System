
package com.toutiao.melon.workerprocess.thread;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ComputedOutput {

    private String streamId;
    private byte[] bytes;
}
