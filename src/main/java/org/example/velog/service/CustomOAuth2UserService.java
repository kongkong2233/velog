package org.example.velog.service;

import lombok.extern.slf4j.Slf4j;
import org.example.velog.entity.Role;
import org.example.velog.entity.User;
import org.example.velog.repository.RoleRepository;
import org.example.velog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String userInfoEndpointUri = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUri();

        if (userInfoEndpointUri != null) {
            String accessToken = userRequest.getAccessToken().getTokenValue();
            String emailEndpointUri = "https://api.github.com/user/emails";

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List> response = restTemplate.exchange(
                    emailEndpointUri, HttpMethod.GET, entity, List.class
            );
            List<Map<String, Object>> emails = response.getBody();

            String primaryEmail = null;
            for (Map<String, Object> email : emails) {
                if ((Boolean) email.get("primary")) {
                    primaryEmail = (String) email.get("email");
                    break;
                }
            }

            Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
            attributes.put("email", primaryEmail);

            saveUser(attributes);

            return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "login");
        }

        return oAuth2User;
    }

    private void saveUser(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        String username = (String) attributes.get("login");
        String defaultNick = username;
        String defaultBlogName = username + ".log";
        String provider = "github";

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (!existingUser.isPresent()) {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setPassword("");
            newUser.setUserNick(defaultNick);
            newUser.setProvider(provider);
            newUser.setBlogName(defaultBlogName);
            newUser.setRegistrationDate(LocalDateTime.now());

            Optional<Role> userRoleOptional = roleRepository.findByRoleName("USER");
            Role userRole;

            if (userRoleOptional.isEmpty()) {
                userRole = new Role();
                userRole.setRoleName("USER");
                userRole = roleRepository.save(userRole);
            } else {
                userRole = userRoleOptional.get();
            }

            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            newUser.setRoles(roles);

            userRepository.save(newUser);
        }
    }
}
