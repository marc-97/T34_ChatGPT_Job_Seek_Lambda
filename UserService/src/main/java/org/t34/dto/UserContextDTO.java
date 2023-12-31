package org.t34.dto;

public class UserContextDTO {
    private String email;
    private String name;
    private String contactNo;
    private String address;
    private String jwtToken;

    public UserContextDTO() {
        this.email = null;
        this.name = null;
        this.contactNo = null;
        this.address = null;
        this.jwtToken = null;
    }

    public UserContextDTO(String email, String name, String contactNo, String address, String jwtToken) {
        this.email = email;
        this.name = name;
        this.contactNo = contactNo;
        this.address = address;
        this.jwtToken = jwtToken;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getAddress() {
        return address;
    }

    public String getJwtToken() {
        return jwtToken;
    }

}
