package org.example.expert.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserResponse2 {

    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;

    public UserResponse2(Long id, String email, String nickname, String profileImageUrl) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }
}
