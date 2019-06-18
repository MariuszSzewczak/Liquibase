package pl.com.migrate.liquibase.fileModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.com.migrate.liquibase.helper.Information;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public class ReadWriteFile {

    private static final Logger log = LoggerFactory.getLogger(ReadWriteFile.class);
    public static String[] upgradeName ={"FIX","PACKAGE", "TRIGGER", "VIEW", "PROCEDURE", "POST_INSTALL"};
    public static String[] installName ={"SEQUENCE", "TYPE", "TABLE", "CONSTRAINT/CONSTRAINT_BASIC", "CONSTRAINT/PK_CONSTRAINT", "CONSTRAINT/FK_CONSTRAINT", "PACKAGE", "TRIGGER", "DATA", "VIEW", "PROCEDURE", "JOB" ,"POST_INSTALL"};
    public static void ShowAnciiLogo(InputStream stream){

        try {
            String parts[] ;
            String name = "";
            StringBuilder newString = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
            String line = "";
            while((line = reader.readLine())!=null) {

                parts = line.split("=");
                name = parts[0];
                newString.append(line).append("\n");
                System.out.println(line);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
     static PrepareInstallationFile pre = new PrepareInstallationFile();
    public static String dirName=null;

    public static void getListDir (String dir) throws IOException {
        File f = new File(dir);
        File[] list = f.listFiles();
         for (File entry : list) {
            if (entry.isDirectory()) {
                dirName=entry.getName();
                getListDir(entry.getAbsolutePath());

            }
            else {//pre.generateStructre(dirName,entry.getName());
            AddLineToFile(new File (entry.getAbsolutePath()));}
        }

    }

    public  List<String> getListDirName (String dir) throws IOException {
        File f = new File(dir);
        File[] list = f.listFiles();
        String dirName=null;
        for (File entry : list) {
            if (entry.isDirectory()) {
                getListDirName(entry.getAbsolutePath());
                dirName=entry.getName();
            }
            //else{pre.generateStructre(dirName,entry.getName());}
        }
        return null;
    }

    public static void AddLineToFile(File f){
        try {
            String parts[] ;
            String name = "";

            StringBuilder newString = new StringBuilder("--liquibase formatted sql" + "\n"
                                                      + "--changeset runOnChange:true stripComments:false" );
            if (f.getName().equals("COMPILE_SCHEMA.sql")) {
                newString.append(" runAlways:true "+ "\n");
            }else { newString.append("\n");}
            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line = "";
            while((line = reader.readLine())!=null)
            {

                parts = line.split("=");
                name = parts[0];
                newString.append(line).append("\n");
            }
            BufferedWriter erasor = new BufferedWriter(new FileWriter(f));
            erasor.write(newString.toString());
            erasor.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
    public static void removeLineFormFile(File f, String fout){
        try {
            String parts[] ;
            String name = "";

            StringBuilder newString = new StringBuilder("BEGIN\n");

            BufferedReader reader = new BufferedReader(new FileReader(f));
            String line = "";
            while((line = reader.readLine())!=null)
            {
             if  (line.contains("INSERT INTO")) {

                parts = line.split("=");
                name = parts[0];
                newString.append(line.replace("install","out")).append("\n");
             }
            }
            newString.append("COMMIT; \n END; \n/");
            BufferedWriter erasor = new BufferedWriter(new FileWriter(fout));
            erasor.write(newString.toString());
            erasor.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    protected static String getFileHeader() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<databaseChangeLog xmlns=\"http://www.liquibase.org/xml/ns/dbchangelog\"\n                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n                   xmlns:ext=\"http://www.liquibase.org/xml/ns/dbchangelog-ext\"\n                   xsi:schemaLocation=\"http://www.liquibase.org/xml/ns/dbchangelog\n                        http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.2.xsd\">\n"+"\n"
;
    }

    protected static String getFileFooter()
    {
        return "    \n</databaseChangeLog>";
    }

    public static void writeToFile(Path outFilePath, String [] includes, String osDirectory) throws IOException {
        Files.deleteIfExists(outFilePath);
        Files.write(outFilePath, getFileHeader().getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.CREATE_NEW});
        for (String s : includes) {
            if ( Information.isWindows() ) {
                Files.write(outFilePath, ("<includeAll  path=\"" + outFilePath.getParent().toString() + Information.osPathSeparator() + "STRUCTURE" + Information.osPathSeparator() + s + Information.osPathSeparator() + "\" />\n").getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.APPEND});
            }
            else {
                Files.write(outFilePath, ("<includeAll  path=\"." + Information.osPathSeparator() + osDirectory +Information.osPathSeparator() + "STRUCTURE" + Information.osPathSeparator() + s + Information.osPathSeparator() + "\" />\n").getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.APPEND});

            }
        }
        Files.write(outFilePath, getFileFooter().getBytes(StandardCharsets.UTF_8), new OpenOption[]{StandardOpenOption.APPEND});
    }
    public static String generateChangeLog (String mode, String outFilePath) throws IOException {
        String fileName=null;
        String outFilePathIdir = outFilePath.replace("out","install");
        if (mode.equals("i")) {writeToFile(Paths.get(outFilePath+"InstallMode.xml"), installName,"out");  fileName=outFilePath+"InstallMode.xml";}
        if (mode.equals("idir")) {writeToFile(Paths.get(outFilePathIdir+"InstallMode.xml"), installName,"install");  fileName=outFilePathIdir+"InstallMode.xml";}
        if (mode.equals("u")) {writeToFile(Paths.get(outFilePath+"UpgradeMode.xml"), upgradeName,"out");  fileName=outFilePath+"UpgradeMode.xml";}
        log.info("File to read:"+fileName);
        return fileName;
    }
}
