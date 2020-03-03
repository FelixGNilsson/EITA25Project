public class Nurse extends User{

    public Nurse(String name, String division) {
        super(name, division);
    }

    @Override
    public String ls() {
        return null;
    }

    @Override
    public String modify() {
        return "not impl";
    }

    @Override
    public String delete() {
        return "not impl";
    }
}
