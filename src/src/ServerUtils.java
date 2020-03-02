import java.io.BufferedReader;
import java.io.PrintWriter;

public class ServerUtils {

    private String userName = null;
    private String password = null;

    public ServerUtils(){
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
            if(userName.equals("a") && password.equals("a")){
                out.println("Successful login");
                out.flush();
                return true;
            }
            else {
                userName = null;
                password = null;
                out.println("Failed login");
                out.flush();
            }
        }
        return false;
    }
}
