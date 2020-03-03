public abstract class User {

    protected String name;
    private String division;

    public User(String name, String division){
        this.name = name;
        this.division = division;
    }

    public abstract String ls();
    public abstract String modify();
    public abstract String delete();

}
