import static spark.Spark.*;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    /*
     * Returns a string with all users in the database
     */
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
     * Creates a new user and hashes the password with a random salt
     * TODO: Find user by ssn
     */
    public boolean createUser(String username, String ssn, String fullname, String role, String division, String password) {
    	//TODO: Create 2FA
    	try {
    		String salt = PasswordManager.getSalt();
        	String hashed = PasswordManager.generatePasswdHash(password, salt);
        	String query = 
        			"INSERT\n"+
        			"INTO users(username, ssn, fullname, role, division, password, salt)\n"+
        			"VALUES (?,?,?,?,?,?,?);";	
        	try(PreparedStatement ps = conn.prepareStatement(query)){
        		ps.setString(1,username);
        		ps.setString(2, ssn);
        		ps.setString(3, fullname);
        		ps.setString(4, role);
        		ps.setString(5, division);
        		ps.setString(6, hashed);
        		ps.setString(7, salt);
        	}catch(SQLException e) {
        		e.printStackTrace();
        		return false;
        	}
        					
        	
        	return true;
        	
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	
    	
    	return false;
    }
    
    /*
     * Lists all journals 
     * TODO: use ssn
     */
    public String listAsUser(String ssn) {
    	String query =
            "SELECT    * \n" +
            "FROM      journals\n" +	
            "WHERE		patient = ?";
    	try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, ssn);
            ResultSet rs = ps.executeQuery();
            String result = JSONizer.toJSON(rs, "data");
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	return "";
    	
    }
    
   /*
    * Lists all journals on a given division
    */
    public String listAsStaff(String division) {
    	String query =
                "SELECT    patient as Name, ssn as SocialSecurity, id as JournalID\n" +
                "FROM      journals\n" +	
                "WHERE		division = ?";
        	try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, division);
                ResultSet rs = ps.executeQuery();
                String result = JSONizer.toJSON(rs, "data");
                return result;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        	return "";
        	
    }
 
    /*
     * Lists all journals
     */
    public String listAsGov() {
    	String query =
                "SELECT    patient as Name, ssn as SocialSecurity, id as JournalID \n" +
                "FROM      journals";
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
     * ONLY FOR TESTING PURPOSES!!!!
     */
    public String viewJournals() {
    	String query =
                "SELECT    * \n" +
                "FROM      journals\n"+
                "JOIN users"+
                "USING(patientssn)";
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
     * Shows the journal with the id journalID and logs which user accessed it
     */
    public String viewJournal(String username, String journalID) {
    	//TODO: Show patientname (MIGHT BE SOLVED)
    	String query =
                "SELECT    * \n" +
                "FROM      journals\n"+
                "JOIN 	   users"+
                "USING     (patientssn)"+
                "WHERE     id = ?";
    	try (PreparedStatement ps = conn.prepareStatement(query)) {
    		ps.setString(1, journalID);
            ResultSet rs = ps.executeQuery();
            logJournalAccess(username, journalID, "Read");
            String result = JSONizer.toJSON(rs, "data");
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	return "";
    }
    
    
    /*
     * Lists all log entries
     */
    public String viewLogs() {
    	String query =
                "SELECT    * \n" +
                "FROM      logs";
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
     * Creates a new log when a given journal is accessed
     */
    private void logJournalAccess(String user, String journalID, String action) {
    	String query =
                "INSERT \n" +
                "INTO      logs(journal, access_date, access_time, user, action)"+
                "VALUES (?,?,?,?,?);";
        	try (PreparedStatement ps = conn.prepareStatement(query)) {
        		ps.setString(1, journalID);
        		ps.setString(2, LocalDate.now().toString());
        		ps.setString(3, LocalTime.now().toString());
        		ps.setString(4, user);
        		ps.setString(5, action);
                
        		ps.executeQuery();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        	
    }
    
    
    /*
     * Creates a journal for a patient and assigns doctor, nurse, and division
     * returns true if journal was successfully created
     * TODO: Control that journal isn't created with non-existing users
     * TODO: return the journal ID
     */
    public boolean createJournal(String patientSSN, String doctorSSN, String nurseSSN, String division) {
    	String query = 
    			"INSERT\n"+
    			"INTO journals(patientssn, doctorssn, nursessn, division)\n"+
    			"VALUES (?,?,?,?);";	
    	try(PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, patientSSN);
    		ps.setString(2, doctorSSN);
    		ps.setString(3, nurseSSN);
    		ps.setString(4, division);
    		logJournalAccess(doctorSSN, null, "Creation");
    	}catch(SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    					
    	return true;
    }
    
    
    /*
     * Edits the content of a journal with the given id and adds a new log.
     */
    public boolean editJournalContent(String userSSN, String journalID, String content) {
    	String query = 
    			"UPDATE journals\n"+
    			"SET content = ?\n"+
    			"WHERE journalid = ?";	
    	try(PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, content);
    		ps.setString(2, journalID);
    		logJournalAccess(userSSN, null, "Edit");
    	}catch(SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    					
    	return true;
    	
    }
    
    
    
    //TODO: Delete journal
    
    
    /*
     * Authenticates user login
     */
    public boolean authenticateUser(String username, String password) {
    	String query =
                "SELECT    password, salt \n" +
                "FROM      users"+
        		"WHERE 	   username = ?";
    	try (PreparedStatement ps = conn.prepareStatement(query)) {
    		ps.setString(1, username);
    		ResultSet rs = ps.executeQuery();
    		String correctHash = rs.getString("password");
    		String salt = rs.getString("salt");
    		String newHash = PasswordManager.generatePasswdHash(password, salt);
    		
    		return correctHash.equals(newHash);
    		
    	} catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	
    	
    	return false;
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
            sb.append("    {\n\t");
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String label = meta.getColumnLabel(i);
                String value = getValue(rs, i, meta.getColumnType(i));
                sb.append("\"" + label + "\": " + value);
                if (i < meta.getColumnCount()) {
                    sb.append("\n\t");
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
