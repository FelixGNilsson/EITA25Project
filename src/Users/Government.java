public class Government extends User {

    public Government(String name, String division) {
        super(name,division);
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
