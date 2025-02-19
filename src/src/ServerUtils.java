import java.io.PrintWriter;

public class ServerUtils {

    private String userName;
    private String password;
    private User currentUser;
    private static Database db;

    private static int USERNAME = 0;
    private static int SALT = 1;
    private static int PASSWORD = 2;
    private static int TYPE_OF_USER = 3;
    private static int DIVISION = 4;

    public ServerUtils(Database db){
        this.db = db;
    }

    public boolean authenticate(String clientMsg, PrintWriter out) {

        if(userName == null){
            userName = clientMsg;
            System.out.println("got " + userName + " as username");
            out.println("recieved username");
            out.flush();
        }else if(password == null){
            password = clientMsg;
            System.out.println("got " + password + " as password");
            String res = db.authenticateUser(userName,password);
            if(!res.equalsIgnoreCase("Failed to authenticate user")){
                out.println("Successful login");
                out.flush();
                String[] info = res.split(":");
                defineUser(userName, info);
                userName = null;
                password = null;
                return true;
            }
            else{
                userName = null;
                password = null;
                out.println("Failed login");
                out.flush();
            }
        }
        return false;
    }

    public String command(String clientMsg){
        String[] command = clientMsg.split(" ");
        //TODO: ta bort felix lösning på problemet
        if(command[0].equals("ls") && command.length == 1){
            return currentUser.ls(db);
        }
        if(command[0].equals("modify") && command.length == 3){
            return currentUser.modify(command[1], command[2],db);
        }
        if(command[0].equals("delete") && command.length == 2){
            return currentUser.delete(command[1],db);
        }
        if(command[0].equals("mkJournal") && command.length == 4){
            return currentUser.mkJournal(command[1], command[2], command[3],db);
        }
        if(command[0].equals("listJournals") && command.length == 1){
            return db.viewJournals(); 
        }
        if(command[0].equals("listLogs") && command.length == 1){
            return db.viewLogs();
        }
        if(command[0].equals("listUsers") && command.length == 1){
            return db.getUsers();
        }
        if(command[0].equals("lscmd")){
            return " ls \n modify JournalID newStatus \n delete JournalID \n mkJournal patient nurse illness\n listJournals\n listLogs\n listUsers \n viewJournal journalID" ;
        }
        if(command[0].equals("viewJournal") && command.length == 2){
            return currentUser.viewJournal(command[1],db);
        }
        return "Unknown command";
    }

    private void defineUser(String user, String[] info){
        //info[role,division]
        if(info[0].equalsIgnoreCase("Doctor")){
            currentUser = new Doctor(user, info[1]);
        } else if(info[0].equalsIgnoreCase("Nurse")){
            currentUser = new Nurse(user, info[1]);
        } else if(info[0].equalsIgnoreCase("Patient")){
            currentUser = new Patient(user,"");
        } else if(info[0].equalsIgnoreCase("Government")){
            currentUser = new Government("","");
        }
    }

}
