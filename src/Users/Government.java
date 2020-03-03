public class Government extends User {

    public Government(String name, String division) {
        super(name,division);
    }

    @Override
    public String ls() {
        //list all
        return "Not implemented";
    }

    @Override
    public String modify(String patient, String status) {
        return "Not Authorized";
    }

    @Override
    public String delete(String patient) {
        //delete row, no questions asked
        return "not impl";
    }
}
