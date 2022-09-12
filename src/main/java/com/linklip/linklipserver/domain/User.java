package com.linklip.linklipserver.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends JpaBaseDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String nickName;

    private String profileImg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Social socialType;

    @Column(nullable = false)
    private String socialId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserState userState;

    // TODO: 현재는 사용되지 않고 있어 SPOTBUGS 불평. 회원탈퇴 기능 추가 시 추가 예정.
    // private LocalDate deletedAt;

    @Builder
    public User(
            String nickName,
            String profileImg,
            Social socialType,
            String socialId,
            UserState userState) {
        this.nickName = nickName;
        this.profileImg = profileImg;
        this.socialType = socialType;
        this.socialId = socialId;
        this.userState = userState;
    }
}
