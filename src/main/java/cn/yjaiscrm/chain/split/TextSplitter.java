package cn.iyque.chain.split;

import java.util.List;

/**
 * 文本切分
 */
public interface TextSplitter {

    /**
     * 文本切分
     *
     * @param content 文本内容
     * @return 切分后的文本列表
     */
    List<String> split(String content);
}
