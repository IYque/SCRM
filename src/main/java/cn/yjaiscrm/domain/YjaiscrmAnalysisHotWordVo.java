package cn.iyque.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IYqueAnalysisHotWordVo {
    //热词
    private String hotWord;

    //热词讨论数量
    private long hotWordDiscussNumber;
}
