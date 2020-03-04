public class Doctor extends User{

    public Doctor(String name, String division) {
        super(name, division);
    }

    @Override
    public String mkJournal(String patient, String nurse, String illness, Database db){
        String s;
        if((s = db.createJournal(patient, name, nurse, division)).isEmpty()){
            return "Creation failed";
        }
        return s;
    }
}
