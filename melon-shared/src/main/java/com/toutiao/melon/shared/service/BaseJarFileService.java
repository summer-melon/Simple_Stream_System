package com.toutiao.melon.shared.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BaseJarFileService {

    private static final Logger log = LoggerFactory.getLogger(BaseJarFileService.class);

    public static final String SUFFIX = ".jar";

    private final Path dataPath;

    public BaseJarFileService(String dataDir) {
        String tmpDir = System.getProperty("java.io.tmpDir");
        dataPath = Paths.get(tmpDir, dataDir);
        if (Files.exists(dataPath)) {
            if (!Files.isDirectory(dataPath)) {
                log.error("Fail to create data dir '" + dataDir + "' in '" + tmpDir
                    + "', since there's a file with the same name exists.");
                System.exit(-1);
            }
            log.info("Data dir '" + dataPath + "' used");
        } else {
            try {
                Files.createDirectory(dataPath);
                log.info("Data dir '" + dataPath + "' was used");
            } catch (IOException e) {
                log.error("{}", e.getMessage());
                System.exit(-1);
            }
        }
    }

    /**
     * 判断Jar包是否存在
     * @param fileName 文件名，不含后缀
     */
    public boolean isJarFileExists(String fileName) {
        Path jarPath = dataPath.resolve(fileName + SUFFIX);
        return Files.exists(jarPath) && Files.isRegularFile(jarPath);
    }

    public void deleteJarFile(String fileName) throws IOException {
        Path jarPath = dataPath.resolve(fileName + SUFFIX);
        Files.delete(jarPath);
    }

    /**
     * 获取用于写入的输出流(覆盖写入)
     * @param fileName same as JobName
     */
    public OutputStream getOutputStream(String fileName) throws IOException {
        Path jarPath = dataPath.resolve(fileName + SUFFIX);
        return Files.newOutputStream(jarPath);
    }

    /**
     * 获取用于读出的输入流
     */
    public InputStream getInputStream(String fileName) throws IOException {
        Path jarPath = dataPath.resolve(fileName + SUFFIX);
        return Files.newInputStream(jarPath);
    }
}
