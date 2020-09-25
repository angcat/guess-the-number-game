package ro.catalin.login;

public class ServerMemory {
    private String username;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ServerMemory(String username) {
        this.username = username;
    }
}
