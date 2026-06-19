package cn.iyque.domain;

import lombok.Data;

import java.util.List;


@Data
public class CustomerChatGroup {
    private String customerId;      // 客户 ID
    private String customerName;    // 客户名称
    private List<String> contents; // 聊天内容列表
    // 构造方法、Getter 和 Setter
    public CustomerChatGroup(String customerId, String customerName, List<String> contents) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.contents = contents;
    }


}
