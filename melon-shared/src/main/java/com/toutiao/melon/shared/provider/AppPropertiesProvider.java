package com.toutiao.melon.shared.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import javax.inject.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppPropertiesProvider implements Provider<Properties> {

    private static final Logger log = LoggerFactory.getLogger(AppPropertiesProvider.class);

    @Override
    public Properties get() {
        Path appPropFilePath = null;
        try {
            appPropFilePath = Paths.get(
                    getClass().getProtectionDomain().getCodeSource().getLocation().toURI()
            ).getParent().resolve("application.properties");
        } catch (URISyntaxException ignored) {
            log.error("Get application properties failed, {}", ignored.toString());
        }

        InputStream appPropFileStream = null;
        if (appPropFilePath != null && Files.exists(appPropFilePath)) {
            try {
                appPropFileStream = Files.newInputStream(appPropFilePath);
            } catch (IOException ignored) {
                log.error("Get application properties failed, {}", ignored.toString());
            }
        }
        appPropFileStream = getClass().getResourceAsStream("/application.properties");
        log.info("Using bundled 'application.properties'");

        Properties prop = new Properties();
        try {
            prop.load(appPropFileStream);
        } catch (IOException e) {
            log.error(e.toString());
            System.exit(-1);
        } finally {
            try {
                assert appPropFileStream != null;
                appPropFileStream.close();
            } catch (IOException e) {
                log.error(e.toString());
            }
        }
        for (String key : prop.stringPropertyNames()) {
            System.setProperty(key, prop.getProperty(key));
        }

        return prop;
    }
}
