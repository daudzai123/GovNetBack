package com.govnet.govnet.dto;

import com.govnet.govnet.entity.MyUser;
import lombok.Data;

@Data
public class UserProfileDTO {

    private final String fullName;
    private final String username;
    private final String email;
    private final String literacyLevel;
    private final String role;
    private final String profileImage;

    public UserProfileDTO(MyUser user) {
        this.fullName = user.getFirstname() + " " + user.getLastname();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.literacyLevel = String.valueOf(user.getLiteracyLevel());
        this.role = String.valueOf(user.getRole());
        this.profileImage = user.getProfileImage(); // adjust if stored differently
    }
}
