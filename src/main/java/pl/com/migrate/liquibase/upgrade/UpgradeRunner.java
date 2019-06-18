package pl.com.migrate.liquibase.upgrade;


import liquibase.integration.commandline.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.com.migrate.liquibase.LiquibaseRunner;
import pl.com.migrate.liquibase.fileModel.Argument;
import pl.com.migrate.liquibase.fileModel.PrepareInstallationFile;
import pl.com.migrate.liquibase.fileModel.ReadWriteFile;
import pl.com.migrate.liquibase.fileModel.Unzipper;
import pl.com.migrate.liquibase.helper.Information;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



public class UpgradeRunner {
    private static final Logger log = LoggerFactory.getLogger(UpgradeRunner.class);

    private String[] generateList(Argument args) throws IOException {

        String logLevel="DEBUG";
        String driver ="oracle.jdbc.OracleDriver";
        String operation ="update";
        Path path = Paths.get(args.upgFile.toString());
        String modeFile = path.getParent().toString().replace("upgFile","out")+ Information.osPathSeparator();
        String changeLogFile = ReadWriteFile.generateChangeLog(args.mode, modeFile);
        String[] run = new String[]{};
        if (args.mode.equals("idir")) {run = new String[] {"--logLevel=" + logLevel, "--driver=" + driver, "--changeLogFile=" + changeLogFile, "--url=" + "offline:oracle?outputLiquibaseSql=true", "changelogSyncSQL"};
        }
        if (args.mode.equals("i") || (args.mode.equals("u"))) {run = new String []{"--logLevel=" + logLevel, "--driver=" + driver, "--changeLogFile=" + changeLogFile, "--url=" + args.url, "--username=" + args.user, "--password=" + args.password, operation};
        }
        //String[] run = {"--logLevel=" + logLevel, "--driver=" + driver, "--changeLogFile=" + changeLogFile, "--url=" + args.url, "--username=" + args.user, "--password=" + args.password, operation};
        //String[] run = {"--logLevel=" + logLevel, "--driver=" + driver, "--changeLogFile=" + changeLogFile, "--url=" + "offline:oracle?outputLiquibaseSql=true", "changelogSyncSQL"};

        return run;
    }

    public int runLiquibase(Argument args) throws IOException, URISyntaxException {
        int res=0;
        Unzipper unz          = new Unzipper();
        Path path = Paths.get(args.upgFile.toString());
        String pathToUnzip = path.getParent().toString().replace("upgFile","out");


        log.info("========== Liquibase ==========");

        if (args.mode.equals("idir")) {
            try {
                PrepareInstallationFile installFile = new PrepareInstallationFile();
                //File file = new File((LiquibaseRunner.class.getClassLoader().getResource("DATABASECHANGELOG.sql")).toURI().getPath());
                File file = new File(pathToUnzip.replace("out","install")+Information.osPathSeparator()+"DATABASECHANGELOG.sql");
                Files.deleteIfExists(Paths.get(path.getParent().toString().replace("upgFile","") +Information.osPathSeparator() +"databasechangelog.csv"));

                installFile.generateStructre(unz.unzipInstallStruct(args.upgFile.toString(),pathToUnzip.replace("out","install") ), pathToUnzip.replace("out","install"));
                // Store console print stream.
                PrintStream ps_console = System.out;
                FileOutputStream fos = new FileOutputStream(file);

                // Create new print stream for file.
                PrintStream ps = new PrintStream(fos);

                // Set file print stream.
                System.setOut(ps);
                Main.run(generateList(args));
                System.setOut(ps_console);
                ReadWriteFile.removeLineFormFile(file, pathToUnzip.replace("out","install")+Information.osPathSeparator()+"DATABASECHANGELOG.sql");
                ps_console.flush();
                ps_console.close();

            } catch (Exception e) {
                log.error("Error running upgrade !!!!" + e.getMessage());
                res=1;
            }

        }
        if (args.mode.equals("i") || (args.mode.equals("u"))){
            try {
                unz.unzip(args.upgFile.toString(),pathToUnzip );
                 Main.run(generateList(args));

        } catch (Exception e) {
            log.error("Error running upgrade !!!!" + e.getMessage());
            res=1;
        }
        }
            return res;

    }

}
