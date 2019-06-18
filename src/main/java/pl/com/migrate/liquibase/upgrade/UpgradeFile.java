package pl.com.migrate.liquibase.upgrade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class UpgradeFile {

    private static final Logger log = LoggerFactory.getLogger(UpgradeFile.class);

    public static void logVersionInfo(File file) {
        log.info("========== Upgrade file full info ==========");
        log.info("file size: " + file.length() + " bytes.");

    }
}
