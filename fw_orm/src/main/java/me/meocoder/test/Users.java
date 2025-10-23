package me.meocoder.test;

import me.meocoder.anotation.Column;
import me.meocoder.anotation.Entity;
import me.meocoder.anotation.Id;
import me.meocoder.anotation.Table;

@Entity
@Table(name = "users")
public class Users {
    @Id
    private Long id;
    @Column(name = "name")
    private String name;

    public Users() {}
    public Users(String name) { this.name = name; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String toString() { return "User(id=" + id + ", name=" + name + ")"; }
}
