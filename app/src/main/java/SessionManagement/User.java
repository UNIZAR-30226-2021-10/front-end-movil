package SessionManagement;

public class User {

    private String email;
    private String username;
    private String points;
    private String coins;


    public User(String email, String username, String points, String coins) {
        this.email = email;
        this.username = username;
        this.points = points;
        this.coins = coins;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getCoins() {
        return coins;
    }

    public void setCoins(String coins) {
        this.coins = coins;
    }
}
