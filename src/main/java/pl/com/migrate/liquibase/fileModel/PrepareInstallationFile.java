package pl.com.migrate.liquibase.fileModel;
import liquibase.change.CheckSum;
import pl.com.migrate.liquibase.helper.Information;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class PrepareInstallationFile {
    Map installFileName = new HashMap();
    Map<String, List<String>> map =new HashMap();


    public void PrepareInstallationFile()
    {
        initizaleMap();
    }
    private void initizaleMap(){

        installFileName.put("STRUCT_INSTALL",	"00_INSTALL"		 );
        installFileName.put("GRANT",			"02_CREATE_SCHEMA"   );
        installFileName.put("SEQUENCE",		    "03_SEQUENCE"		 );
        installFileName.put("TYPE",				"04_TYPE"            );
        installFileName.put("TABLE",			"05_TABLE"           );
        installFileName.put("CONSTRAINT_BASIC","06_CONSTRAINT_BASIC" );
        installFileName.put("FK_CONSTRAINT",	"08_FK_CONSTRAINT"   );
        installFileName.put("PK_CONSTRAINT",	"07_PK_CONSTRAINT"   );
        installFileName.put("DATA",				"09_DATA"            );
        installFileName.put("PACKAGE",			"10_PACKAGE"         );
        installFileName.put("VIEW",				"11_VIEW"            );
        installFileName.put("PROCEDURE",		"12_PROCEDURE"       );
        installFileName.put("TRIGGER",			"13_TRIGGER"         );
        installFileName.put("JOB",				"14_JOB"             );
        installFileName.put("POST_INSTALL",		"15_POST_INSTALL"    );

    }
    protected  String getFileHeader(String spoolFileName) {
        return "set serveroutput on size 200000 \n spool "+spoolFileName+".sql.log\n SET TERMOUT ON \n ";
    }

    protected  String getFileFooter()
    {
        return "SET DEFINE ON \n  spool off";
    }
    protected  String getInder(String spoolFileName, String deep)
    {
        if (deep.equals("F"))
        {
            if(spoolFileName.equals("01_PARAMETERS") || spoolFileName.equals("02_CREATE_SCHEMA") )
            {
                return "\n\nprompt ==================== \n  prompt "+spoolFileName+".log\n   prompt ==================== \n @"+spoolFileName+"\n\n";

            }
            return "\n\nprompt ==================== \n  prompt "+spoolFileName+".log\n   prompt ==================== \n ALTER SESSION SET CURRENT_SCHEMA = \"&&SCHEMA_USER\";\n  @"+spoolFileName+"\n\n";
        }
        else
        return "\n\nprompt ==================== \n  prompt "+spoolFileName+".log\n   prompt ==================== \n  @"+spoolFileName+"\n\n";
    }
    public void generateStructre(Map<String, List<String>> map, String out) throws IOException {
        initizaleMap();
        ArrayList<String> set = new ArrayList<String>();
        for (String key : map.keySet()) {

            for (Object ek : installFileName.keySet()) {
                if ( key.contains(ek.toString()) ) {

                    set.add(installFileName.get(ek).toString());
                    writeToFile(Paths.get(out + Information.osPathSeparator() + installFileName.get(ek).toString() + ".sql"), map.get(key), installFileName.get(ek).toString(), "S");
                }
            }
        }
        set.add("01_PARAMETERS");
        set.add("DATABASECHANGELOG");
        Collections.sort(set);
        writeToFile(Paths.get(out + Information.osPathSeparator() + "00_INSTALL.sql"), set, "00_INSTALL", "F");
        writeToFile(Paths.get(out + Information.osPathSeparator() + "DATABASECHANGELOG.sql"), new ArrayList<>(), "DATABASECHANGELOG", "F");
        createparamFile(Paths.get(out + Information.osPathSeparator() + "01_PARAMETERS.sql"),"APP", "qwe123","WZL1SC");
    }

    public  void writeToFile(Path outFilePath, List<String> includes, String mainFileName, String deeper) throws IOException {
        String startFile = null;
        Files.createDirectories(outFilePath);
        Files.deleteIfExists(outFilePath);
        Files.write(outFilePath, getFileHeader(mainFileName).getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.CREATE_NEW});
        if(mainFileName=="00_INSTALL"){
            Files.write(outFilePath, ( "\n WHENEVER SQLERROR EXIT FAILURE ROLLBACK  \n WHENEVER OSERROR EXIT FAILURE ROLLBACK").getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.APPEND});
            }
        for (String s : includes) {
             Files.write(outFilePath, ( getInder( s,deeper)).getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.APPEND});
        }
        if (outFilePath.toString().contains("00_INSTALL")){startFile="\n EXIT";}
        Files.write(outFilePath, (getFileFooter()+startFile).getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.APPEND});

    }

    protected void createparamFile(Path outFilePath, String schemaName, String schemaPassword, String defaultTablespace) throws IOException {
        Files.createDirectories(outFilePath);
        Files.deleteIfExists(outFilePath);
        Files.write(outFilePath, getFileHeader("01_PARAMETERS").getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.CREATE_NEW});
        Files.write(outFilePath, ("def SCHEMA_USER="+schemaName+"\n def SCHEMA_PASSWORD="+schemaPassword+"\n def DEFAULT_TABLESPACE="+defaultTablespace+"\n spool off ").getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.APPEND});
    }
}