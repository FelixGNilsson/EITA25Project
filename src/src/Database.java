import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;


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
        //generateSampleUsers();
        //generateSampleJournals();
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
    
    
    private void generateSampleUsers() {
    	createUser("doctorA", "Arnold Arnoldsson", "doctor", "general", "doctorA");
    	createUser("doctorB", "Bob Bobsson", "doctor", "ortho", "doctorB");
    	createUser("doctorC", "Charlie Charliesson", "doctor", "plastic", "doctorC");
    	createUser("nurseA", "Abra Cadabra", "nurse", "general", "nurseA");
    	createUser("nurseB", "Barbra Cadabra", "nurse", "ortho", "nurseB");
    	createUser("nurseC", "Cadaver Cadabra", "nurse", "plastic", "nurseC");
    	createUser("patientA", "Ant Doe", "patient", "general", "patientA");
    	createUser("patientB", "Barbie Doe", "patient", "Ortho", "patientB");
    	createUser("patientC", "Carmen Doe", "patient", "plastic", "patientC");
    	createUser("gov", "Government", "government", "", "gov");
    }
    
    private void generateSampleJournals() {
    	createJournal("patientA", "doctorA", "nurseA", "general");
    	createJournal("patientB", "doctorB", "nurseB", "ortho");
    	createJournal("patientC", "doctorC", "nurseC", "plastic");


    }
    
    /*
     * Creates a new user and hashes the password with a random salt
     */
    public boolean createUser(String username,  String fullname, String role, String division, String password) {
    	//TODO: Create 2FA
    	try {
    		String salt = PasswordManager.getSalt();
        	String hashed = PasswordManager.generatePasswdHash(password, salt);
        	String query = 
        			"INSERT\n"+
        			"INTO users(username, fullname, role, division, password, salt)\n"+
        			"VALUES (?,?,?,?,?,?);";	
        	try(PreparedStatement ps = conn.prepareStatement(query)){
        		ps.setString(1,username);
        		ps.setString(2, fullname);
        		ps.setString(3, role);
        		ps.setString(4, division);
        		ps.setString(5, hashed);
        		ps.setString(6, salt);
        		ps.executeUpdate();
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
     */
    public String listAsUser(String username) {
    	String query =
            "SELECT    * \n" +
            "FROM      journals\n" +	
            "WHERE		patient = ?";
    	try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
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
                "SELECT    patient as Name, id as JournalID\n" +
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
                "SELECT    patient as Name, id as JournalID \n" +
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
                "FROM      journals\n";
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
     * TODO: make sure that the user has the rights to view journal
     */
    public String viewJournal(String username, String journalID) {
    	if(!getDivision(username).equals(getDivisionFromJournal(journalID))) {
    		return "";
    	}
    	if(getRole(username).equals("nurse") && !username.equals(getNurseFromJournal(journalID))) {
			return "";
    	}
    	
    	String query =
                "SELECT    * \n" +
                "FROM      journals\n"+
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
                
        		ps.executeUpdate();
                
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
    public String createJournal(String patient, String doctor, String nurse, String division) {
    	String statement = 
    			"INSERT\n"+
    			"INTO journals(patient, doctor, nurse, division)\n"+
    			"VALUES (?,?,?,?);";	
    	try(PreparedStatement ps = conn.prepareStatement(statement)){
    		ps.setString(1, patient);
    		ps.setString(2, doctor);
    		ps.setString(3, nurse);
    		ps.setString(4, division);
    		ps.executeUpdate();
    		String query =
                    "SELECT    id \n" +
                    "FROM      journals\n"+
                    "WHERE    rowid = last_insert_rowid()";
        	try (PreparedStatement ps2 = conn.prepareStatement(query)) {
                ResultSet rs = ps2.executeQuery();
                String id = rs.getString("id");
                logJournalAccess(doctor, id, "Creation");
                return id;
            } catch (SQLException e) {
                e.printStackTrace();
            }
    	}catch(SQLException e) {
    		e.printStackTrace();
    		return "";
    	}
    					
    	return "";
    }
    
    
    /*
     * Edits the content of a journal with the given id and adds a new log.
     */
    public boolean editJournalContent(String user, String journalID, String content) {
    	if(!getDivision(user).equals(getDivisionFromJournal(journalID))) {
    		return false;
    	}
    	if(getRole(user).equals("nurse") && !user.equals(getNurseFromJournal(journalID))) {
			return false;
    	}
    	
    	String query = 
    			"UPDATE journals\n"+
    			"SET content = ?\n"+
    			"WHERE id = ?";	
    	try(PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, content);
    		ps.setString(2, journalID);
    		logJournalAccess(user, null, "Edit");
    	}catch(SQLException e) {
    		e.printStackTrace();
    		return false;
    	}
    					
    	return true;
    	
    }
    
    
    
    //TODO: Delete journal
    
    
    /*
     * Authenticates user login
     * TODO: returnera relevant information (role and division)
     */
    public String authenticateUser(String username, String password) {
    	String query =
                "SELECT    password, salt, role, division \n" +
                "FROM      users \n"+
        		"WHERE 	   username = ? ";
    	try (PreparedStatement ps = conn.prepareStatement(query)) {
    		ps.setString(1, username);
    		ResultSet rs = ps.executeQuery();
    		String correctHash = rs.getString("password");
    		String salt = rs.getString("salt");
    		String role = rs.getString("role");
    		String division = rs.getString("division");
    		String newHash = PasswordManager.generatePasswdHash(password, salt);
    		
    		if(correctHash.equals(newHash)) {
    	    	StringBuilder sb = new StringBuilder();
    			sb.append(role);
        		sb.append(":");
        		sb.append(division);
        		
        		return sb.toString();
    		}	
    	} catch (SQLException e) {
    		return "Failed to authenticate user";
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    	
    	
    	return "Failed to authenticate user";
    }
    
    
    private String getDivision(String username) {
    	String query = 
    			"SELECT division\n"+
    			"FROM users\n"+
    			"WHERE username = ?";	
    	try(PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, username);
    		ResultSet rs = ps.executeQuery();
    		return rs.getString("division");
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    //TODO: get nurse from journal
    
    private String getNurseFromJournal(String journalID) {
    	String query = 
    			"SELECT nurse\n"+
    			"FROM journals\n"+
    			"WHERE id = ?";	
    	try(PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, journalID);
    		ResultSet rs = ps.executeQuery();
    		return rs.getString("nurse");
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
    	return null;
    }  
    
    private String getDivisionFromJournal(String journalID) {
    	String query = 
    			"SELECT division\n"+
    			"FROM journals\n"+
    			"WHERE id = ?";	
    	try(PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, journalID);
    		ResultSet rs = ps.executeQuery();
    		return rs.getString("division");
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
    	return null;
    }    
    private String getRole(String username) {
    	String query = 
    			"SELECT role\n"+
    			"FROM users\n"+
    			"WHERE username = ?";	
    	try(PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, username);
    		ResultSet rs = ps.executeQuery();
    		return rs.getString("role");
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
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
