package cn.iyque.domain;


import cn.hutool.core.annotation.Alias;
import lombok.Data;

@Data
public class GiteeTokenResponse {
    @Alias("access_token")
    private String accessToken;

    @Alias("token_type")
    private String tokenType;

    @Alias("expires_in")
    private Integer expiresIn;

    @Alias("refresh_token")
    private String refreshToken;

    @Alias("scope")
    private String scope;

    @Alias("created_at")
    private Long createdAt;
}
