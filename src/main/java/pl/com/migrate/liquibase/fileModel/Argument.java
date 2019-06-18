package pl.com.migrate.liquibase.fileModel;

import org.apache.commons.cli.CommandLine;
import java.io.File;

public class Argument {

    public String url;
    public String user;
    public String password;
    public File upgFile;
    public String mode;
    public String help;

    public Argument() { }
    public Argument(CommandLine cmd)
    {
        this.url        = cmd.getOptionValue("url");
        this.user       = cmd.getOptionValue("U");
        this.password   = cmd.getOptionValue("p");
        this.upgFile    = new File(cmd.getOptionValue("upgFile"));
        this.mode       = cmd.getOptionValue("mode");
        this.help       = cmd.getOptionValue("help");

    }

   public String toString() {
        return  "    --url=" + url +"\n"+
                "    --user=" + user +"\n"+
                "    --password=" + password +"\n"+
                "    --upgFile=" + upgFile +"\n"+
                "    --mode=" + mode +"\n";
    }
}
