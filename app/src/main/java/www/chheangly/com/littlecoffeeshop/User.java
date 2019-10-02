package www.chheangly.com.littlecoffeeshop;

public class User
{
    private String name;
    private String email;
    private String title;
    private String password;
    private String image_url;

    public User(String name, String email, String title, String password, String image_url)
    {
        this.name = name;
        this.email = email;
        this.title = title;
        this.password = password;
        this.image_url = image_url;
    }
}
