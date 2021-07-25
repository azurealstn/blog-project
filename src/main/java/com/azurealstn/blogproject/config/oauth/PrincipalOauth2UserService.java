package com.azurealstn.blogproject.config.oauth;

import com.azurealstn.blogproject.config.auth.PrincipalDetail;
import com.azurealstn.blogproject.config.oauth.provider.FacebookUserInfo;
import com.azurealstn.blogproject.config.oauth.provider.GoogleUserInfo;
import com.azurealstn.blogproject.config.oauth.provider.OAuth2UserInfo;
import com.azurealstn.blogproject.domain.user.Role;
import com.azurealstn.blogproject.domain.user.User;
import com.azurealstn.blogproject.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;

        if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String password = UUID.randomUUID().toString();
        String email = oAuth2User.getAttribute("email");
        String nickname = "소셜 로그인";
        Role role = Role.USER;

        Optional<User> userEntity = userRepository.findByUsername(username);

        User user;
        if (userEntity.isPresent()) {
            user = userEntity.get();
        } else {
            user = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .nickname(nickname)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(user);

        }

        return new PrincipalDetail(user, oAuth2User.getAttributes());
    }
}
