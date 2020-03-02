import java.io.BufferedReader;
import java.io.IOException;

public class ServerUtils {
    BufferedReader read;

    public ServerUtils(BufferedReader read){
        this.read = read;
    }

    public void authenticate() throws IOException {
        Boolean auth = false;

        //Authentication by password
        while(!auth){
            String userName;
            String psw;

            System.out.println("Username?");
            userName = read.readLine();
            System.out.println("Password?");
            psw = read.readLine();

            if(userName.equals("a") && psw.equals("a")){
                System.out.println("Bra jobbat!");
                auth = true;
            }
            else{
                System.out.println("Sluta hacka mig");
            }
        }
    }
}
