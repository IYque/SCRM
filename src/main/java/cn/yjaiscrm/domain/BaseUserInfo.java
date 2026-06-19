package cn.iyque.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseUserInfo {
    //企业id
    private String corpId;

    //应用id
    private String agentId;
}
