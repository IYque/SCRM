package cn.iyque.enums;


import cn.hutool.core.collection.ListUtil;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 操作日志枚举类型
 */
    @Getter
    @SuppressWarnings("all")
    public enum OperateLogType {

        A(1,"成员操作类型", ListUtil.toList(
                SubType.builder()
                        .subCode(1)
                        .value("添加外部联系人")
                        .build(),
                SubType.builder()
                        .subCode(2)
                        .value("删除外部联系人")
                        .build(),
                SubType.builder()
                        .subCode(3)
                        .value("标记企业客户")
                        .build(),
                SubType.builder()
                        .subCode(4)
                        .value("新设备登录")
                        .build(),
                SubType.builder()
                        .subCode(5)
                        .value("更换手机号")
                        .build(),
                SubType.builder()
                        .subCode(6)
                        .value("绑定微信号")
                        .build(),
                SubType.builder()
                        .subCode(7)
                        .value("换绑微信号")
                        .build(),
                SubType.builder()
                        .subCode(8)
                        .value("邀请成员")
                        .build(),
                SubType.builder()
                        .subCode(9)
                        .value("封禁登录")
                        .build(),
                SubType.builder()
                        .subCode(11)
                        .value("修改昵称")
                        .build(),
                SubType.builder()
                        .subCode(12)
                        .value("修改姓名")
                        .build(),
                SubType.builder()
                        .subCode(13)
                        .value("副设备登录")
                        .build(),
                SubType.builder()
                        .subCode(15)
                        .value("确认高级功能订单")
                        .build(),
                SubType.builder()
                        .subCode(16)
                        .value("应用变更")
                        .build(),
                SubType.builder()
                        .subCode(17)
                        .value("确认会话内容存档订单")
                        .build()
        )),
        B(2,"管理员操作类型", ListUtil.toList(
                SubType.builder()
                        .subCode(2)
                        .value("权限管理变更")
                        .build(),
                SubType.builder()
                        .subCode(3)
                        .value("成员与部门变更")
                        .build(),
                SubType.builder()
                        .subCode(7)
                        .value("其它")
                        .build(),
                SubType.builder()
                        .subCode(8)
                        .value("应用变更")
                        .build(),
                SubType.builder()
                        .subCode(11)
                        .value("通讯录与聊天管理")
                        .build(),
                SubType.builder()
                        .subCode(12)
                        .value("企业信息管理")
                        .build(),
                SubType.builder()
                        .subCode(13)
                        .value("外部联系人管理")
                        .build()
        ));

        private Integer groupType;

        private String groupMsg;

        private List<SubType> subTypeList;


        OperateLogType(Integer groupType,String groupMsg,List<SubType> subTypeList){
            this.groupType=groupType;
            this.groupMsg=groupMsg;
            this.subTypeList=subTypeList;
        }


    /**
     * 根据groupType获取对应的SubType列表
     * @param groupType 分组类型
     * @return 对应的SubType列表，如果找不到返回空列表
     */
    public static List<SubType> getSubTypesByGroupType(Integer groupType) {
        return Arrays.stream(values())
                .filter(type -> type.getGroupType().equals(groupType))
                .findFirst()
                .map(OperateLogType::getSubTypeList)
                .orElse(Collections.emptyList());
    }

        @Data
        @Builder
        public static class SubType {
            private Integer subCode;
            private String value;

        }
    }
