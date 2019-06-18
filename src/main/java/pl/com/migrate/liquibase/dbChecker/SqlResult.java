package pl.com.migrate.liquibase.dbChecker;

public class SqlResult {

    public String ObjectName;
    public String ObjectType;
    public String Status;

    public String toString() {
        return  "    ObjectName=" + ObjectName +
                "    ObjectType=" + ObjectType +
                "    Status=" + Status;
    }
}
