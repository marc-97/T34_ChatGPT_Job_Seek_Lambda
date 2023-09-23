package org.t34.dto;

public class UserContextDTO {
    private String email;
    private String name;
    private String contactNo;
    private String address;
    private String jwtToken;
    private Status status;

    public UserContextDTO(Status status) {
        this.email = null;
        this.name = null;
        this.contactNo = null;
        this.address = null;
        this.jwtToken = null;
        this.status = status;
    }

    public UserContextDTO(String email, String name, String contactNo, String address, String jwtToken, Status status) {
        this.email = email;
        this.name = name;
        this.contactNo = contactNo;
        this.address = address;
        this.jwtToken = jwtToken;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
