package cn.iyque.domain;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


/**
 * 统计查询条件
 */
@Data
public class IYQueCountQuery {
   private  Long userCodeId;
   @DateTimeFormat(pattern = "yyyy-MM-dd")
   private Date startTime;
   @DateTimeFormat(pattern = "yyyy-MM-dd")
   private Date endTime;
}
