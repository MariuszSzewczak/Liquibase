package pl.com.migrate.liquibase;

import liquibase.exception.LiquibaseException;
import liquibase.logging.core.DefaultLoggerConfiguration;
import liquibase.statement.ExecutablePreparedStatementBase;
import org.apache.commons.cli.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.com.migrate.liquibase.dbChecker.DataBaseConnection;
import pl.com.migrate.liquibase.fileModel.Argument;
import pl.com.migrate.liquibase.fileModel.PrepareInstallationFile;
import pl.com.migrate.liquibase.fileModel.ReadWriteFile;
import pl.com.migrate.liquibase.fileModel.Unzipper;
import pl.com.migrate.liquibase.helper.CmdLineArgsParser;
import pl.com.migrate.liquibase.helper.Information;
import pl.com.migrate.liquibase.upgrade.UpgradeRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

public class LiquibaseRunner {
    private static final Logger logger = LoggerFactory.getLogger(LiquibaseRunner.class);

    public static void main(String[] args) throws SQLException, ClassNotFoundException, LiquibaseException, IOException, URISyntaxException {
        DefaultLoggerConfiguration de = new DefaultLoggerConfiguration();
        de.setLogLevel("DEBUG");

        UpgradeRunner runner = new UpgradeRunner();
        CommandLine commandLine = CmdLineArgsParser.parseArgs(args);
        Unzipper unz          = new Unzipper();
        Argument argument       = new Argument(commandLine);
        DataBaseConnection conn = new DataBaseConnection();
        PrepareInstallationFile installFile = new PrepareInstallationFile();
        int mainRes = 0;

        logger.info("SC Upgrade Tool command line args: \n"+argument.toString());

        ReadWriteFile.ShowAnciiLogo(LiquibaseRunner.class.getClassLoader().getResourceAsStream("ScToolAncii"));

        Path path = Paths.get(commandLine.getOptionValue("upgFile"));

        Information.printJavaInformation();
        Information.printJvmMemory();

        logger.info("Entering in "+Information.getModeInformation(argument.mode)+" mode.");

        logger.info("Test connection to database host:"+argument.url);

        if (conn.CheckConnection(argument.url, argument.user, argument.password) || argument.mode.equals("idir")) {
            logger.info("Connection status: SUCCESS");
            logger.info("Start verification INVALID objects ");
            //conn.CheckInvalidObjects(argument.url, argument.user, argument.password, argument.mode);
            mainRes=runner.runLiquibase(argument);

        }
        if ( mainRes == 0 && (argument.mode.equals("i") || argument.mode.equals("u")))
        {
            System.out.println("Start verification INVALID objects after Liquibase update");
            conn.CheckInvalidObjects(argument.url, argument.user, argument.password,argument.mode);
            //conn.dbVersion(argument.url, argument.user, argument.password,path.getFileName().toString(), argument.mode);
            System.out.println("Tool say GoodBye.");

        }
        else
        {
            System.out.println("Central System Upgrade Tool has a errors during the process please check the logs");

        }
    }

}