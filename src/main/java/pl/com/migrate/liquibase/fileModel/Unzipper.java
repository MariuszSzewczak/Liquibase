package pl.com.migrate.liquibase.fileModel;
import liquibase.change.CheckSum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import java.io.*;
import java.io.IOException;
import java.nio.file.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Unzipper {
    private static final Logger log = LoggerFactory.getLogger(Unzipper.class);
    PrepareInstallationFile pre = new PrepareInstallationFile();

    private javax.crypto.SecretKey generateKey(byte[] key) throws NoSuchAlgorithmException {
        if (System.getProperty("os.name").toUpperCase().equals("AIX")) {
            return new javax.crypto.spec.SecretKeySpec(new byte[] { -101, -87, -67, -43, 64, 18, 12, -42, 68, 98, 107, -32, -117, 53, -61, 92 }, "AES");
        }
        KeyGenerator keygen = null;
        try {
            keygen = KeyGenerator.getInstance("AES", "SunJCE");
        }
        catch (NoSuchAlgorithmException e) {}catch (NoSuchProviderException e)
        {
            keygen = KeyGenerator.getInstance("AES");
        }
        if (key != null) {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key);
            keygen.init(128, random);
        } else { return null; }
        return keygen.generateKey();
    }
    private InputStream decrypt(InputStream encrypted, byte[] key)
    {
        Cipher decC = null;
        try {
            decC = Cipher.getInstance("AES/ECB/PKCS5Padding");
        }
        catch (Exception ex) {}
        try
        {
            decC.init(2, generateKey(key));
        } catch (NullPointerException e) {
            log.error("Upgrade error.");
        } catch (InvalidKeyException e) {
            log.error("Invalid upgrade key.");
        } catch (NoSuchAlgorithmException e) {
            log.error("Invalig algorithm.");
        }

        CipherInputStream cis = new CipherInputStream(encrypted, decC);

        return new java.io.DataInputStream(cis);
    }
    public void unzip(final String zipFilePath, final String unzipLocation) throws IOException {
        log.info("Reading upgrade file content...");
        cleanOutSpace(unzipLocation);
        if (!(Files.exists(Paths.get(unzipLocation)))) {
            Files.createDirectories(Paths.get(unzipLocation));
        }
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                if ( entry.getName().startsWith("STRUCTURE") ) {
                    Path filePath = Paths.get(unzipLocation, entry.getName());
                    if ( !entry.isDirectory() ) {
                        unzipFiles(zipInputStream, filePath);
                    } else {
                        Files.createDirectories(filePath);
                    }
                }
                zipInputStream.closeEntry();
                entry = zipInputStream.getNextEntry();
            }
        }
        ReadWriteFile.getListDir(unzipLocation.replace("upgFile","out")+"/STRUCTURE");
        log.info("output directory "+unzipLocation);
        log.info("Reading process finished.");

    }

    public  Map<String, List<String>> unzipInstallStruct(final String zipFilePath, final String unzipLocation) throws IOException {
        Map<String, List<String>> map =new HashMap();
        List <String> fileName = new ArrayList<>();
        List <String> f;

        log.info("Reading upgrade file content...");
        cleanOutSpace(unzipLocation);
        if (!(Files.exists(Paths.get(unzipLocation)))) {
            Files.createDirectories(Paths.get(unzipLocation));
        }
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            while (entry != null) {
                if ( entry.getName().startsWith("STRUCTURE") ) {

                    Path filePath = Paths.get(unzipLocation, entry.getName());
                    if ( !entry.isDirectory() ) {
                        unzipFiles(zipInputStream, filePath);
                        fileName.add(entry.getName());

                    } else {

                        map.put(entry.getName(), null);

                        fileName.clear();
                        Files.createDirectories(filePath);

                    }
                }
                    zipInputStream.closeEntry();
                    entry = zipInputStream.getNextEntry();

            }
            for(String key: map.keySet()) {
            f = new ArrayList<>();

                for (String s : fileName) {
                    if(s.contains(key))
                        f.add(s);
                }
                map.replace(key,f);
            }
        }

        //ReadWriteFile.getListDir(unzipLocation.replace("upgFile","out")+"/STRUCTURE");
        log.info("output directory "+unzipLocation);
        log.info("Reading process finished.");
        return map;
    }

    public void unzipFiles(final ZipInputStream zipInputStream, final Path unzipFilePath) throws IOException {

        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzipFilePath.toAbsolutePath().toString()))) {
            byte[] bytesIn = new byte[1024];
            int read = 0;
            while ((read = zipInputStream.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    public void cleanOutSpace (final String directoryName)
    {
        File f = new File(directoryName);
        File[] list = f.listFiles();

        if (list == null) {
            return;
        }

        for (File entry : list) {

            if (entry.isDirectory()) {
                cleanOutSpace(entry.getAbsolutePath());
            entry.delete();
        }
        else{entry.delete();}
     }
  }
}