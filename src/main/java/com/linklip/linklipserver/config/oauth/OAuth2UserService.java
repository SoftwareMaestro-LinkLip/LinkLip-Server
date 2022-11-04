package com.linklip.linklipserver.config.oauth;

import com.linklip.linklipserver.config.auth.PrincipalDetails;
import com.linklip.linklipserver.domain.Social;
import com.linklip.linklipserver.domain.User;
import com.linklip.linklipserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private User user;

    // 구글로 부터 받은 UserRequest 데이터에 대한 후처리되는 함수 (구글로 부터 회원프로필 받음)
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName =
                userRequest
                        .getClientRegistration()
                        .getProviderDetails()
                        .getUserInfoEndpoint()
                        .getUserNameAttributeName();

        OAuth2Attributes oAuth2Attributes =
                OAuth2Attributes.of(
                        registrationId, userNameAttributeName, oAuth2User.getAttributes());

        String socialId = Social.GOOGLE + "_" + oAuth2User.getAttributes().get("sub");

        if (userRepository.findBySocialId(socialId).isPresent()) {
            user = userRepository.findBySocialId(socialId).get();
        } else {
            user = userRepository.save(oAuth2Attributes.toEntity());
        }

        return new PrincipalDetails(user);
    }
}
