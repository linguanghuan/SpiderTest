package lingh.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Mysql && SQL Server DBHelper
 * @author antgan
 * http://www.cnblogs.com/miercler/p/5599465.html
 */
public class DBHelper {
    public static final String driver_class = "com.mysql.jdbc.Driver";
    public static final String driver_url = "jdbc:mysql://127.0.0.1/linghtest?useunicode=true&characterEncoding=utf8";
    public static final String user = "root";
    public static final String password = "";

    private static Connection conn = null;
    private PreparedStatement pst = null;
    private ResultSet rst = null;
    /**
     * Connection
     */
    public DBHelper() {
        try {
            conn = DBHelper.getConnInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ����ģʽ
     * �߳�ͬ��
     * @return
     */
    private static synchronized Connection getConnInstance() {
        if(conn == null){
            try {
                Class.forName(driver_class);
                conn = DriverManager.getConnection(driver_url, user, password);
                System.out.println("�������ݿ�ɹ�");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }


    /**
     * close
     */
    public void close() {

        try {
            if (conn != null) {
                DBHelper.conn.close();
            }
            if (pst != null) {
                this.pst.close();
            }
            if (rst != null) {
                this.rst.close();
            }
            System.out.println("�ر����ݿ�ɹ�");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * query
     * 
     * @param sql
     * @param sqlValues
     * @return ResultSet
     */
    public ResultSet executeQuery(String sql, List<String> sqlValues) {
        try {
            pst = conn.prepareStatement(sql);
            if (sqlValues != null && sqlValues.size() > 0) {
                setSqlValues(pst, sqlValues);
            }
            rst = pst.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rst;
    }

    /**
     * update
     * 
     * @param sql
     * @param sqlValues
     * @return result
     */
    public int executeUpdate(String sql, List<String> sqlValues) {
        int result = -1;
        try {
            pst = conn.prepareStatement(sql);
            if (sqlValues != null && sqlValues.size() > 0) {
                setSqlValues(pst, sqlValues);
            }
            result = pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * sql set value
     * 
     * @param pst
     * @param sqlValues
     */
    private void setSqlValues(PreparedStatement pst, List<String> sqlValues) {
        for (int i = 0; i < sqlValues.size(); i++) {
            try {
                pst.setObject(i + 1, sqlValues.get(i));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
 