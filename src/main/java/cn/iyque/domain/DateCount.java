package cn.iyque.domain;

import lombok.Data;
import java.util.Date;


@Data
public class DateCount {

    private Date date;
    private int count;

    public DateCount(Date date, int count) {
        this.date = date;
        this.count = count;
    }

}
