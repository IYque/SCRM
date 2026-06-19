package cn.iyque.domain;


import cn.iyque.entity.IYqueUser;
import lombok.Data;

import java.util.List;

@Data
public class IYqueCustomerSeasVo {

    private List<IYqueUser> allocateUsers;
}
