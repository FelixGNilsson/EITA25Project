public class Patient extends User{

    public Patient(String name,String division) {
        super(name, division);
    }

    @Override
    public String ls() {
        return null;
    }


    @Override
    public String modify() {
        return "Not Authorized";
    }

    @Override
    public String delete() {
        return "Not Authorized";
    }
}
