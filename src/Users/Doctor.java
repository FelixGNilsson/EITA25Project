public class Doctor extends User{

    public Doctor(String name, String division) {
        super(name, division);
    }

    @Override
    public String mkPatient(String patient, String nurse, String illness, String pwd, Database db){
        //create record using DIVISION:DOCTOR:NURSE:PATIENT:ILLNESS ,
        // AND create Account with Certificate script, and account USERNAME:SALT:PASSWORD:TYPE_OF_USER:DIVISION
        return "Not Implemented";
    }
}
