public class Government extends User {

    public Government(String name, String division) {
        super(name,division);
    }

    @Override
    public String ls(Database db) {
        return db.listAsGov();
    }

    @Override
    public String modify(String patient, String status, Database db) {
        return "Not Authorized";
    }

    @Override
    public String delete(String journalID, Database db) {
        if(db.deleteJournal(name,journalID)){
            return "Successful removal of " + journalID;
        }
        return "Failed removal";
    }
}
