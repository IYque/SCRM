package cn.iyque.enums;


import cn.iyque.domain.SubType;

import java.util.ArrayList;
import java.util.List;

/**
 * 企业微信文件操作来源
 */
public enum FileSecuritySource {

        /**
         * 聊天场景（编码：401）
         */
        CHAT(401, "聊天"),
        /**
         * 邮件场景（编码：402）
         */
        MAIL(402, "邮件"),
        /**
         * 文档场景（编码：403）
         */
        DOCUMENT(403, "文档"),
        /**
         * 微盘场景（编码：404）
         */
        MICRO_DISK(404, "微盘"),
        /**
         * 日程场景（编码：405）
         */
        SCHEDULE(405, "日程"),
        /**
         * 会议场景（编码：406）
         */
        MEETING(406, "会议"),
        /**
         * 审批场景（编码：407）
         */
        APPROVAL(407, "审批"),
        /**
         * 汇报场景（编码：408）
         */
        REPORT(408, "汇报"),
        /**
         * 收集表场景（编码：409）
         */
        COLLECTION_FORM(409, "收集表"),
        /**
         * 客户联系场景（编码：410）
         */
        CUSTOMER_CONTACT(410, "客户联系"),
        /**
         * 上下游场景（编码：411）
         */
        UPSTREAM_DOWNSTREAM(411, "上下游"),
        /**
         * 收藏场景（编码：450）
         */
        FAVORITE(450, "收藏"),
        /**
         * 文件列表场景（编码：451）
         */
        FILE_LIST(451, "文件列表"),
        /**
         * 其他场景（编码：452）
         */
        OTHER(452, "其他");

        private final int code;          // 操作来源编码（唯一标识）
        private final String description; // 操作来源描述（语义化说明）

        /**
         * 枚举构造函数（私有，通过枚举常量初始化）
         * @param code 操作来源编码
         * @param description 操作来源描述
         */
        FileSecuritySource(int code, String description) {
            this.code = code;
            this.description = description;
        }

        /**
         * 获取操作来源编码
         * @return 操作来源编码（如401、402等）
         */
        public int getCode() {
            return code;
        }

        /**
         * 获取操作来源描述
         * @return 操作来源描述（如"聊天"、"邮件"等）
         */
        public String getDescription() {
            return description;
        }

        /**
         * 根据编码获取对应的操作来源枚举常量<br>
         * 若编码不存在，抛出IllegalArgumentException
         * @param code 操作来源编码
         * @return 对应的OperationSource枚举常量
         * @throws IllegalArgumentException 当编码无效时抛出
         */
        public static FileSecuritySource fromCode(int code) {
            for (FileSecuritySource source : FileSecuritySource.values()) {
                if (source.getCode() == code) {
                    return source;
                }
            }
            throw new IllegalArgumentException("无效的操作来源编码: " + code);
        }


        /**
         * 将枚举FileSecurityOperation转换为List<SubType>
         * @return 包含所有枚举值的SubType列表
         */
        public static List<SubType> getSubTypeListFromEnum() {
                List<SubType> subTypeList = new ArrayList<>();
                // 遍历枚举常量数组
                for (FileSecuritySource source : FileSecuritySource.values()) {
                        // 将枚举的code和description映射到SubType的subCode和value
                        SubType subType = SubType.builder()
                                .subCode(source.getCode())
                                .value(source.getDescription())
                                .build();
                        subTypeList.add(subType);
                }
                return subTypeList;
        }

}
