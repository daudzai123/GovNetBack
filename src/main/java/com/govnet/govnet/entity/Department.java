package com.govnet.govnet.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long depId;
    private String depName;
    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_dep_Id")
    @JsonIgnoreProperties(
            value = { "depName","description","parentDepartmentId","users" },
            allowSetters = true
    )
    private Department parentDepartmentId;

    @ToString.Exclude
    @ManyToMany(mappedBy = "department", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(
            value = { "lastName","position","active","department",
                    "profilePhoto","email","password", "role" },
            allowSetters = true
    )
    private List<MyUser> users;
    @JsonIgnore
    public List<MyUser> getActiveUsers() {
        return this.users.stream()
                .filter(MyUser::getIsActive)  // Assuming 'isAcitve' is the getter for the 'active' property
                .collect(Collectors.toList());
    }
}
