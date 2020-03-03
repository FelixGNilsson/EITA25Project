public abstract class User {

    protected String name;
    private String division;
    protected RecordsManager rm;

    public User(String name, String division){
        rm = new RecordsManager();
        this.name = name;
        this.division = division;
    }

    public abstract String ls();
    public abstract String modify();
    public abstract String delete();

}
