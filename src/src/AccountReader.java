import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class AccountReader {

    public AccountReader(){
    }

    public String[] getAccountInformation(String userName){
        String line;
        try{
            BufferedReader reader = new BufferedReader(new FileReader("projekt1/Accounts.txt"));
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                String[] lineArray = line.split(":"); //DELIMITER?


                if(lineArray[0].equals(userName)){
                    return lineArray;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
