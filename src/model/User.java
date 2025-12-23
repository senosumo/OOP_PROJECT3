package model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String address;

    public User(int id, String username, String password, String role, String address) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
