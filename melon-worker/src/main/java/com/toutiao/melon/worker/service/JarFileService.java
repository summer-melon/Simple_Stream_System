

package com.toutiao.melon.worker.service;


import com.toutiao.melon.shared.service.BaseJarFileService;

import javax.inject.Singleton;

@Singleton
public class JarFileService extends BaseJarFileService {

    public JarFileService() {
        super("melon-worker");
    }
}
