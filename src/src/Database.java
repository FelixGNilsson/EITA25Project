import static spark.Spark.*;
import java.sql.*;
import java.util.*;
import spark.*;

public class Database {

    /**
     * The database connection.
     */
    private Connection conn;

    /**
     * Creates the database interface object. Connection to the
     * database is performed later.
     */
    public Database(String filename) {
        openConnection(filename);
    }

    /**
     * Opens a connection to the database, using the specified
     * filename 
     */
    public boolean openConnection(String filename) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + filename);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Closes the connection to the database.
     */
    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the connection to the database has been established
     * 
     * @return true if the connection has been established
     */
    public boolean isConnected() {
        return conn != null;
    }

    /* ================================== */
    /* --- insert your own code below --- */
    /* ===============================*== */

    public String getUsers() {
        String query =
            "SELECT    * \n" +
            "FROM      users";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            String result = JSONizer.toJSON(rs, "data");

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    
    
    /*

    public String getStudent(Request req, Response res, String id) {
        String query =
            "SELECT    s_id AS id, s_name AS name, gpa\n" +
            "FROM      students\n" +
            "WHERE     s_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            String result = JSONizer.toJSON(rs, "data");
            res.status(200);
            res.body(result);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String addStudent(Request req, Response res) {
        String statement =
            "INSERT\n" +
            "INTO     students(s_id, s_name, gpa, size_hs)\n" +
            "VALUES   (?, ?, ?, ?)\n";
        try (PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, req.queryParams("id"));
            ps.setString(2, req.queryParams("name"));
            ps.setString(3, req.queryParams("gpa"));
            ps.setString(4, req.queryParams("sizeHs"));
            if (ps.executeUpdate() != 1) {
                res.status(400);
                return "nothing happened";
            }
            String query =
                "SELECT   s_id\n" +
                "FROM     students\n" +
                "WHERE    rowid = last_insert_rowid()\n";
            try (PreparedStatement ps2 = conn.prepareStatement(query)) {
                ResultSet rs = ps2.executeQuery();
                if (rs.next()) {
                    String newId = rs.getString("s_id");
                    String result = String.format("{ 'id': %s", newId);
                    res.status(201);
                    res.body(result);
                    return result;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
    */
}


/**
 * Auxiliary class for automatically translating a ResultSet to JSON
 */
class JSONizer {

    public static String toJSON(ResultSet rs, String name) throws SQLException {
        StringBuilder sb = new StringBuilder();
        ResultSetMetaData meta = rs.getMetaData();
        boolean first = true;
        sb.append("{\n");
        sb.append("  \"" + name + "\": [\n");
        while (rs.next()) {
            if (!first) {
                sb.append(",");
                sb.append("\n");
            }
            first = false;
            sb.append("    {");
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String label = meta.getColumnLabel(i);
                String value = getValue(rs, i, meta.getColumnType(i));
                sb.append("\"" + label + "\": " + value);
                if (i < meta.getColumnCount()) {
                    sb.append(", ");
                }
            }
            sb.append("}");
        }
        sb.append("\n");
        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }
    
    private static String getValue(ResultSet rs, int i, int columnType) throws SQLException {
        switch (columnType) {
        case java.sql.Types.INTEGER:
            return String.valueOf(rs.getInt(i));
        case java.sql.Types.REAL:
        case java.sql.Types.DOUBLE:
        case java.sql.Types.FLOAT:
            return String.valueOf(rs.getDouble(i));
        default:
            return "\"" + rs.getString(i) + "\"";
        }
    }

}
