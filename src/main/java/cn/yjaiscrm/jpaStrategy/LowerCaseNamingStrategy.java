package cn.iyque.jpaStrategy;

import org.hibernate.boot.model.naming.Identifier;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;

public class LowerCaseNamingStrategy extends SpringPhysicalNamingStrategy {

    protected Identifier getIdentifier(String name, boolean quoted) {
        // 将名称转换为小写
        return new Identifier(name.toLowerCase(), quoted);
    }
}
