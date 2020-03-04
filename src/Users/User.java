public abstract class User {

    protected String name;
    protected String division;

    public User(String name, String division){
        this.name = name;
        this.division = division;
    }

    public String ls(Database db){
        //default functionality, return all rows within users division
        return db.listAsStaff(division);
    }
    public String modify(String journalID, String status, Database db){
        //default, find patient row, check division and change status if correct division
        if(db.editJournalContent(name,journalID,status)){
            return journalID + " updated to " + status;
        }
        else {
            return "modification failed";
        }

    }
    public String delete(String patient, Database db){
        //define in government
        return "Not Authorized";
    }
    public String mkJournal(String patient, String nurse, String illness, Database db){
        //defined in doctor
        return "Not Authorized";
    }

    public String viewJournal(String journalID, Database db){
        return db.viewJournal(name, journalID);
    }



}
