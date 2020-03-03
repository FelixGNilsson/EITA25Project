public class Patient extends User{

    public Patient(String name,String division) {
        super(name, division);
    }

    @Override
    public String ls(Database db) {
        //Only return row with this name
        return null;
    }

    @Override
    public String modify(String patient, String status, Database db) {
        return "Not Authorized";
    }

}
