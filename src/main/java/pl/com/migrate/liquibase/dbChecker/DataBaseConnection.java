package pl.com.migrate.liquibase.dbChecker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.com.migrate.liquibase.helper.Information;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseConnection {
    private static final Logger log = LoggerFactory.getLogger(DataBaseConnection.class);

    public boolean CheckConnection(String host, String user, String password) throws SQLException, ClassNotFoundException {
        boolean res1 = false;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection(host, user, password);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL");
            while (rs.next()) {
                res1 = rs.getBoolean(1);
            }
            con.close();
        }
        catch(Exception e){res1=false;}
        return res1;

    }
     public void CheckInvalidObjects(String host, String user, String password, String mode) throws SQLException, ClassNotFoundException {
        if (mode.equals("idir")) {return;}
         SqlResult res ;
         List<SqlResult> objectList = new ArrayList<>();
         Class.forName("oracle.jdbc.driver.OracleDriver");
         Connection c  = GetConnection(host, user, password);
         Statement stmt = c.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT OBJECT_NAME, OBJECT_TYPE, STATUS from user_objects where status <>'VALID'");
         while (rs.next()) {
             res = new SqlResult();
             res.ObjectName = rs.getString(1);
             res.ObjectType = rs.getString(2);
             res.Status     = rs.getString(3);
             objectList.add(res);
         }
         if (objectList.size() > 0) {
             for (SqlResult r : objectList) {
                 log.info("Process with be interrupted to time resolve the problem in:  " + r.toString());

             }System.exit(0);
         }
         else
             log.info("Schema without INVALID objects.");
         c.close();
     }
    public Connection GetConnection(String host, String user, String password) throws SQLException, ClassNotFoundException {

        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(host, user, password);
    }
    protected boolean isDbVersionExists(Connection c, String lastDbVersion) throws SQLException, ClassNotFoundException {

        boolean checker=false;
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Statement stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery("select NVL(max(nr),'FIRST') from ( SELECT NR FROM DB_VERSION ORDER BY INSTALLATION_DATE DESC ) where rownum=1");
        while (rs.next())
    {
        if( rs.getString(1).equals(lastDbVersion)){
            checker=true;
        }
        else checker= false;
    }
        return checker;
    }

    public void dbVersion(String host, String user, String password, String fileName, String mode) throws SQLException, ClassNotFoundException {


        String dbNr = fileName.replace("sql-data-","").replace("-SNAPSHOT.jar","");
        String sql="INSERT INTO DB_VERSION(NR, TYPE, COMMIT, IS_CURRENT) values ('"+dbNr+"','"+ Information.getModeInformation(mode) +"','NEED WORK','Y')";
        log.info(sql);
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection c  = GetConnection(host, user, password);
        if(isDbVersionExists(c, dbNr)){
            c.close();
            return;
        }
        else {
            Statement stmt = c.createStatement();
            stmt.executeUpdate(sql);
            c.close();
        }
    }
}
