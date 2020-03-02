import java.io.BufferedReader;
import java.io.PrintWriter;

public class ServerUtils {

    private String userName;
    private String password;
    private AccountReader accReader;

    public ServerUtils(){
        accReader = new AccountReader();
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
            String[] account = accReader.getAccountInformation(userName);

            if(account != null){
                for(int i = 0; i < account.length; i++){
                    System.out.println(account[i]);
                }
            } else {
                System.out.println("account null when user:" + userName + " and pwd: " + password);
            }

            if(account != null && password.equals(account[2])){
                out.println("Successful login");
                out.flush();
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
}
