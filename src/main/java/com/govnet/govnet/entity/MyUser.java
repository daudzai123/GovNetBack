package com.govnet.govnet.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.govnet.govnet.enums.LiteracyLevel;
import com.govnet.govnet.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    private String fathername;

    @Column(unique = true)
    private String nid;

    @Column(unique = true)
    private String phone;

    @Enumerated(EnumType.STRING)
    private LiteracyLevel literacyLevel;

    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "boolean default true")
    private Boolean isActive = true;


    @Column(updatable = false)
    private LocalDate createDate;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "position")
    private String position;

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnoreProperties(
            value = { "parentDepartmentId", "description","users" },
            allowSetters = true
    )
    @JoinTable(name = "user-department",
            joinColumns = @JoinColumn(name = "user-id"),
            inverseJoinColumns = @JoinColumn(name = "department-id")
    )

    private List<Department> department;

}
