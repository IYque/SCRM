package cn.iyque.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

public class SnowFlakeUtils implements IdentifierGenerator {
    /**
     * 派号器workid：0~31
     * 机房datacenterid：0~31
     */
    private static Snowflake snowflake = IdUtil.createSnowflake(1, 1);

    public static Long nextId() {
        return snowflake.nextId();
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object o) throws HibernateException {

        return nextId();
    }
}
