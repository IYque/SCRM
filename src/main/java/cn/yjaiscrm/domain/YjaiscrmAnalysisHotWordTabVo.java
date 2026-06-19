package cn.iyque.domain;


import lombok.Data;

@Data
public class IYqueAnalysisHotWordTabVo {

    /**
     * 讨论总数
     */
    private long discussTotalNumber;


    /**
     * 昨日讨论总数
     */
    private long ydDiscussTotalNumber;


    /**
     * 昨日客户焦点热词
     */
    private String ydHotWord;


    /**
     * 昨日客户焦点类别
     */
    private String ydHotWordCategory;
}
