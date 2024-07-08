package org.example.velog.service;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
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
            return new DefaultOAuth2User(oAuth2User.getAuthorities(), attributes, "login");
        }

        return oAuth2User;
    }
}
