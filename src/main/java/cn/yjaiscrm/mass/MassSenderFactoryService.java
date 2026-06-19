package cn.iyque.mass;

import cn.iyque.mass.sender.AbstractMassSender;
import cn.iyque.mass.sender.CustomerMassSender;
import cn.iyque.mass.sender.GroupMassSender;
import org.springframework.stereotype.Component;


@Component
public class MassSenderFactoryService {
    public  AbstractMassSender createSender(String chatType) {
        switch (chatType) {
            case "group"://客群群发
                return new GroupMassSender();
            case "single"://客户群发
                return new CustomerMassSender();
            default:
                throw new IllegalArgumentException("不支持的群发类型");
        }

    }

    }
