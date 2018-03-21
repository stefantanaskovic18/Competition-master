package glavni;
import java.sql.*;

public class DB
{
    private Connection conn;
    private static DB instanca;

    private DB()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:Nrt7814_djordje_milenkovic.sql");
        }catch (Exception e)
        {
            System.err.println("Greska pri konektovanju na bazu podataka"+e.getMessage());
            System.exit(1);
        }
    }

    public ResultSet select(String sql) throws SQLException
    {
        Statement statement = conn.createStatement();
        return statement.executeQuery(sql);
    }

    public int uidQuery(String sql) throws SQLException
    {
        Statement statement  = conn.createStatement();
        return statement.executeUpdate(sql);
    }

    public static DB getInstanca()
    {
        if(instanca == null)
            instanca = new DB();
        return instanca;
    }
}
