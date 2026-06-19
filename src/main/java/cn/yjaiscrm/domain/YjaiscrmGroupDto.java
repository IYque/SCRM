package cn.iyque.domain;

import lombok.Data;

import java.util.List;

@Data
public class IYQueGroupDto {


    //群id
    private String chatId;

    //标签ids
    private List<String> tagIds;
}
