package cn.iyque.domain;


import lombok.Data;

import java.util.List;

@Data
public class EmployeeChatGroup {
    private String employeeId;       // 员工 ID
    private String employeeName;    // 员工名称
    private List<CustomerChatGroup> customerChatGroups; // 客户分组列表

    // 构造方法、Getter 和 Setter
    public EmployeeChatGroup(String employeeId, String employeeName, List<CustomerChatGroup> customerChatGroups) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.customerChatGroups = customerChatGroups;
    }


}
