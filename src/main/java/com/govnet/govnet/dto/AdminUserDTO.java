package com.govnet.govnet.dto;

import com.govnet.govnet.enums.LiteracyLevel;
import com.govnet.govnet.enums.Role;

public class AdminUserDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String fathername;
    private String nid;
    private String phone;
    private LiteracyLevel literacyLevel;
    private String email;
    private String username;
    private Role role;
    private boolean isHead;

    // Constructor (without isHead, optional)
    public AdminUserDTO(Long id, String firstname, String lastname, String fathername, String nid,
                        String phone, LiteracyLevel literacyLevel, String email, String username,
                        Role role) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.fathername = fathername;
        this.nid = nid;
        this.phone = phone;
        this.literacyLevel = literacyLevel;
        this.email = email;
        this.username = username;
        this.role = role;
    }

    // Optionally, you could add an overloaded constructor that includes isHead
    public AdminUserDTO(Long id, String firstname, String lastname, String fathername, String nid,
                        String phone, LiteracyLevel literacyLevel, String email, String username,
                        Role role, boolean isHead) {
        this(id, firstname, lastname, fathername, nid, phone, literacyLevel, email, username, role);
        this.isHead = isHead;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFathername() {
        return fathername;
    }

    public void setFathername(String fathername) {
        this.fathername = fathername;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LiteracyLevel getLiteracyLevel() {
        return literacyLevel;
    }

    public void setLiteracyLevel(LiteracyLevel literacyLevel) {
        this.literacyLevel = literacyLevel;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setIsHead(boolean isHead) {
        this.isHead = isHead;
    }
}
