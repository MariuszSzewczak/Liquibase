package pl.com.migrate.liquibase.helper;

import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;
public class CmdLineArgsParser
{
    public CmdLineArgsParser() {}


    public static CommandLine parseArgs(String... args) {
        Options options = new Options();

        CommandLineParser commandLineParser = new DefaultParser();

        options.addOption("help",    "help",               false,"Show this help");
        options.addOption("url",     "jdbc-url",           true, "JDBC Connection url");
        options.addOption("U",       "user name",          true, "JDBC User name");
        options.addOption("p",       "jdbc-arch-password", true, "JDBC Schema User password");
        options.addOption("mode",    "mode",               true, "Install or upgrade mode, available arguments u -upgrade, i -install, idir -create file to install from scratch SQLPlus");
        options.addOption("upgFile", "upgrade-file",       true, "Path to upgrade file");

        CommandLine commandLine = null;

        try {
            commandLine = commandLineParser.parse(options, args, true);

            if (commandLine.hasOption("help")) {
                HelpFormatter formater = new HelpFormatter();
                formater.printHelp("SC Upgrade Tool", options);
                System.exit(0);
            }

            if (!validate(commandLine)) {
                throw new Exception("Incorrect args.");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(40);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(40);
        }
        return commandLine;
    }

    static boolean validate(CommandLine cl) throws Exception
    {
        if (!cl.hasOption("upgFile")) throw new Exception("param 'upgFile' is required.");
        if (!new File(cl.getOptionValue("upgFile")).exists()) {
            throw new Exception("arg for param 'upgFile' is incorrect. File: " + cl.getOptionValue("upgFile") + " does not exist.");
        }
        if (!cl.hasOption("url")) throw new Exception("param 'url' is required.");
        if (!cl.hasOption("mode")) throw new Exception("param 'mode' is required.");


        return true;
    }
    }
