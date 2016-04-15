package me.syniuhin.storyteller.net.model;

/**
 * infm created it with love on 4/15/16. Enjoy ;)
 */
public class User {
  private String username;
  private String email;
  private String password;

  public static User create() {
    return new User();
  }

  public User setUsername(String username) {
    this.username = username;
    return this;
  }

  public User setEmail(String email) {
    this.email = email;
    return this;
  }

  public User setPassword(String password) {
    this.password = password;
    return this;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}
