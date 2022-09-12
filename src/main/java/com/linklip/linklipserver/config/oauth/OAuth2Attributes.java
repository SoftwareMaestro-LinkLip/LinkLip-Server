package com.linklip.linklipserver.config.oauth;

import com.linklip.linklipserver.domain.Social;
import com.linklip.linklipserver.domain.User;
import com.linklip.linklipserver.domain.UserState;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class OAuth2Attributes {

    private String name;
    private String picture;
    private Social socialType;
    private String socialId;
    private UserState userState;

    @Builder
    public OAuth2Attributes(
            String name, String picture, Social socialType, String socialId, UserState userState) {
        this.name = name;
        this.picture = picture;
        this.socialType = socialType;
        this.socialId = socialId;
        this.userState = userState;
    }

    // userNameAttributeName은 해당 서비스의 map의 키값이 되는 값이됩니다. {google="sub", kakao="id",
    // naver="response"}
    public static OAuth2Attributes of(
            String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if (registrationId.equals("google")) {
            return ofGoogle(userNameAttributeName, attributes);
        }
        // TODO: 카카오 소셜 로그인 추가 시 수정
        //        else if (registrationId.equals("kakao")) {
        //            return ofKakao(userNameAttributeName, attributes);
        //        }
        // TODO 예외처리
        return null;
    }

    private static OAuth2Attributes ofGoogle(
            String userNameAttributeName, Map<String, Object> attributes) {
        String socialId = Social.GOOGLE + "_" + attributes.get(userNameAttributeName);
        return OAuth2Attributes.builder()
                .socialId(socialId)
                .name((String) attributes.get("name"))
                .picture((String) attributes.get("picture"))
                .socialType(Social.GOOGLE)
                .socialId(socialId)
                .userState(UserState.USED)
                .build();
    }

    // TODO: 카카오 소셜 로그인 추가 시 수정
    //    private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object>
    // attributes) {
    //        Map<String, Object> kakao_account = (Map<String, Object>)
    // attributes.get("kakao_account");  // 카카오로 받은 데이터에서 계정 정보가 담긴 kakao_account 값을 꺼낸다.
    //        Map<String, Object> profile = (Map<String, Object>) kakao_account.get("profile");   //
    // 마찬가지로 profile(nickname, image_url.. 등) 정보가 담긴 값을 꺼낸다.
    //
    //        return new OAuth2Attributes(attributes,
    //            userNameAttributeName,
    //            (String) profile.get("nickname"),
    //            (String) profile.get("profile_image_url"));
    //    }

    public User toEntity() {
        return User.builder()
                .nickName(name)
                .profileImg(picture)
                .socialType(socialType)
                .socialId(socialId)
                .userState(userState)
                .build();
    }
}
