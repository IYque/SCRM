package cn.iyque.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IYqueSummaryKfMsgDto {
    //客户名称
    private String nickname;

    //客户头像
    private String avatar;

    //客户id
    private String externalUserId;


    //客户unionid
    private String unionid;


    //聊天开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;


    //聊天结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;


    //聊天内容
    private List<String> kfMsgs;
}
