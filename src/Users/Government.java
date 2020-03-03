public class Government extends User {

    public Government(String name, String division) {
        super(name,division);
    }

    @Override
    public String ls(Database db) {
        //list all
        return db.listAsGov();
    }

    //TODO implement
    @Override
    public String modify(String patient, String status, Database db) {
        return "Not Authorized";
    }

    @Override
    public String delete(String patient, Database db) {
        //delete row, no questions asked
        return "not impl";
    }
}
