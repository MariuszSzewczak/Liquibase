package pl.com.migrate.liquibase.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Information {
    private static final Logger log = LoggerFactory.getLogger(Information.class);

    public static final String[] javaPropKeys = { "java.version", "java.home", "java.vendor", "java.vm.name", "java.vm.version", "java.vm.vendor", "java.vm.info", "java.runtime.name", "java.runtime.version", "os.arch", "os.name", "os.version", "sun.cpu.endian" };

    public static void printJavaInformation() {
        log.info("========== Basic information ==========");
        for (String k : javaPropKeys) {
            log.info(k + ": " + System.getProperty(k, "unknown"));
        }
    }
    public static boolean isWindows() {
        return System.getProperty("os.name").startsWith("Windows ");
    }
    public static String osPathSeparator()
    {
        String sep;
        if (System.getProperty("os.name","unknown").contains("Windows")) {
            sep ="\\";
        }
        else sep ="/";

        return sep;
    }
     public static String getModeInformation(String mode)
   {
       String m =null;
       switch (mode) {
           case "u":
               m=" upgrade";
               break;
           case "i":
               m=" install ";
               break;
           case "idir":
               m=" generate install files ";
               break;
                    }
    return m;

   }
    public static void printJvmMemory()
    {
        try
        {
            Runtime runtime = Runtime.getRuntime();
            log.info("========== Memory information ==========");
            log.info("jvm max memory: " + runtime.maxMemory());
            log.info("jvm total memory: " + runtime.totalMemory());
            log.info("jvm free memory: " + runtime.freeMemory());
        }
        catch (Exception e)
        {
            log.warn("Can't read jvm memory settings.");
        }
    }
}
