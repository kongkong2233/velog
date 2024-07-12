package org.example.velog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userId;
    private String username;
    private String password;
    private String email;
    private String userNick;
    private String blogName;
    private LocalDateTime registrationDate;
    private String provider;

    public void setBlogNameFromEmail() {
        this.blogName = this.username + ".log";
    }
}
