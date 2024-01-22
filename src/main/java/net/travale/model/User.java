package net.travale.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import net.travale.model.converter.RolesSetToStringConverter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="USERS")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name="USER_NAME", columnDefinition="varchar(256)", nullable = false, unique = true)
    private String userName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="PASSWORD", columnDefinition="varchar(256)", nullable = false)
    private String password;

    @Column(name="ROLES", columnDefinition="varchar(256)", nullable = false)
    @Convert(converter = RolesSetToStringConverter.class)
    private Set<Role> roles = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name="PASSWORD_RESET_TOKEN", columnDefinition="varchar(256)", nullable = true)
    private String passwordResetToken;
    
    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    @JsonIgnore
    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public User setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
        return this;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public User setRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }


}
