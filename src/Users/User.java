public abstract class User {

    protected String name;
    private String division;

    public User(String name, String division){
        this.name = name;
        this.division = division;
    }

    public String ls(Database db){
        //default functionality, return all rows within users division
        return "Not implemented";
    }
    public String modify(String patient, String status, Database db){
        //default, find patient row, check division and change status if correct division
        return "Not implemented";
    }
    public String delete(String patient, Database db){
        //define in government
        return "Not Authorized";
    }
    public String mkPatient(String patient, String nurse, String illness, String pwd, Database db){
        //defined in doctor
        return "Not Authorized";
    }


}
