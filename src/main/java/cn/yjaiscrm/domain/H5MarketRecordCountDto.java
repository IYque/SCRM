package cn.iyque.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class H5MarketRecordCountDto {
    /**
     *  日期
     */
    private String date;

    /**
     * H5访问总数
     */
    private long viewTotalNumber;

    /**
     * h5访问总人数
     */
    private long viewTotalPeopleNumber;

    /**
     * 今日h5访问总数
     */
    private long tdViewTotalNumber;


    /**
     * 今日h5访问总人数
     */
    private long tdViewTotalPeopleNumber;

    public H5MarketRecordCountDto() {
    }

    public H5MarketRecordCountDto(String date, long viewTotalNumber, long viewTotalPeopleNumber, long tdViewTotalNumber, long tdViewTotalPeopleNumber) {
        this.date = date;
        this.viewTotalNumber = viewTotalNumber;
        this.viewTotalPeopleNumber = viewTotalPeopleNumber;
        this.tdViewTotalNumber = tdViewTotalNumber;
        this.tdViewTotalPeopleNumber = tdViewTotalPeopleNumber;
    }
}
