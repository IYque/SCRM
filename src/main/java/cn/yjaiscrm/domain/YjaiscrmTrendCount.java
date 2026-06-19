package cn.iyque.domain;

import lombok.Data;

import java.util.List;

@Data
public class IYQueTrendCount {
    private List<String> xData;
    private List<List<Integer>> series;
}
