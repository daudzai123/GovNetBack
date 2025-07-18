package com.govnet.govnet.dto;

import com.govnet.govnet.entity.MyUser;
import lombok.Data;

@Data
public class UserMinimalDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String username;

    public UserMinimalDTO(MyUser user) {
        this.id = user.getId();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.username = user.getUsername();
    }
}
