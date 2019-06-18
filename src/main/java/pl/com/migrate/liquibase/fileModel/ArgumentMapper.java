package pl.com.migrate.liquibase.fileModel;

import pl.com.migrate.liquibase.fileModel.Argument;

import java.io.File;

public class ArgumentMapper {


    public Argument getSplitList(String [] args) {

        Argument arg = new Argument();
        for (String arg1 : args) {
            String[] result = arg1.split("=");
            if (result[0].equals("--url")) {
                arg.url = result[1];
            }
            if (result[0].equals("--user")) {
                arg.user = result[1];
            }
            if (result[0].equals("--password")) {
                arg.password = result[1];
            }
            if (result[0].equals("--upgFile")) {
                arg.upgFile = new File(result[1]);
            }
            if (result[0].equals("--mode")) {
                arg.mode = result[1];
            }
        }
        return arg;
    }
}
