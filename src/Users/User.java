public abstract class User {

    private String division;

    public User(String division){
        this.division = division;
    }

    public abstract void ls();
    public abstract void modify();
    public abstract void delete();

}
