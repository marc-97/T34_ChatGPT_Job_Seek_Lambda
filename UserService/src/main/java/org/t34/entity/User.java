package org.t34.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.t34.dto.UserContextDTO;
import org.t34.util.GeneralHelper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "user")
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "userId")
    Long userId;

    @Column(name = "email")
    String email;

    @Column(name = "password")
    String password;

    @Column(name = "name")
    String name;

    @Column(name = "contactNo")
    String contactNo;

    @Column(name = "address")
    String address;

    public User() {
    }

    public User(Long userId, String email, String hashedPassword) {
        this.userId = userId;
        this.email = email;
        password = hashedPassword;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserContextDTO toUserContextDTO() {
        String jwtToken = GeneralHelper.encodeJWT(userId.toString());
        return new UserContextDTO(email, name, contactNo, address, jwtToken);
    }
}
