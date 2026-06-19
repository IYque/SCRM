package cn.iyque.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThreeLoginInfo {
    //是否启动三方登录，默认是不启动
    private boolean startThreeLogin;

    //三方登录地址
    private String threeLoginUrl;
}
