package cn.iyque.domain;

import lombok.Data;

import java.util.List;

@Data
public class IYQueCustomerDto {
    //员工id
    private String userId;
    //客户id
    private String externalUserid;

    //标签ids
    private List<String> tagIds;
}
