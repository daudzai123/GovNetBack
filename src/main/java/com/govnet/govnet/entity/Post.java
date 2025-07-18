package com.govnet.govnet.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String media;
    private boolean isUrgent;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    @JsonIgnoreProperties(value = {
            "password", "department", "role", "email", "nid", "phone",
            "literacyLevel", "createDate", "profileImage", "position", "isActive"
    }, allowSetters = true)
    private MyUser author;

    @ManyToMany
    @JoinTable(
            name = "post_visible_departments",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    @JsonIgnoreProperties({"users", "parentDepartmentId"})
    private List<Department> visibleDepartments = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
}