package cn.iyque.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Data
@TableName("iyque_session_intercept_rule")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IYqueSessionInterceptRule extends BaseEntity {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 敏感词列表
     */
    private String sensitiveWords;

    /**
     * 员工ID列表
     */
    private String staffIds;

    /**
     * 拦截类型（1:警告并拦截;2:仅警告）
     */
    private Integer interceptType;


    /**
     * 1：手机号、2：邮箱地:、3：红包；若为空表示清除所有的语义规则
     */
    private String semantics;


    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;


    /**
     * 企业微信返回的规则id
     */
    private String ruleId;



    /**
     * 0:正常;1:删除;
     */
    @TableLogic
    private Integer delFlag;


    /**
     * 获取 B 中有、但 A 中没有的员工 ID 列表（基于逗号分隔字符串）
     *
     * @param aStaffIds A 对象的 staffIds（如 "c,d,g"）
     * @param bStaffIds B 对象的 staffIds（如 "c,f,g"）
     * @return 差集列表，如 ["f"]
     */
    public static List<String> getDiffStaffIds(String aStaffIds, String bStaffIds) {
        if (bStaffIds == null || bStaffIds.trim().isEmpty()) {
            return Collections.emptyList();
        }

        // 构建 A 的员工 ID 集合（去重 + 忽略空值）
        Set<String> aSet = parseToSet(aStaffIds);

        // 从 B 中过滤出不在 A 中的 ID
        return Arrays.stream(bStaffIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .filter(s -> !aSet.contains(s))
                .collect(Collectors.toList());
    }

    // 辅助方法：安全地将逗号分隔字符串转为 Set
    private static Set<String> parseToSet(String input) {
        if (input == null || input.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }


    public static List<Integer> parseSemanticsStrict(String semantics) {
        if (semantics == null || semantics.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(semantics.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    try {
                        return Integer.valueOf(s);
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("无效的语义ID: '" + s + "'，必须为整数");
                    }
                })
                .collect(Collectors.toList());
    }
}