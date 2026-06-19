package cn.iyque.enums;

import cn.iyque.domain.SubType;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 企微文件操作类型
 */
public enum FileSecurityOperation {
        UPLOAD(101, "上传"),
        NEW_FOLDER(102, "新建文件夹"),
        DOWNLOAD(103, "下载"),
        UPDATE(104, "更新"),
        STAR(105, "星标"),
        MOVE(106, "移动"),
        COPY(107, "复制"),
        RENAME(108, "重命名"),
        DELETE(109, "删除"),
        RESTORE(110, "恢复"),
        PERMANENT_DELETE(111, "彻底删除"),
        FORWARD_TO_WECHAT_WORK(112, "转发到企业微信"),
        DOWNLOAD_VIA_LINK(113, "通过链接下载"),
        GET_SHARE_LINK(114, "获取分享链接"),
        MODIFY_SHARE_LINK(115, "修改分享链接"),
        CLOSE_SHARE_LINK(116, "关闭分享链接"),
        FAVORITE(117, "收藏"),
        NEW_DOCUMENT(118, "新建文档"),
        NEW_SPREADSHEET(119, "新建表格"),
        OPEN(121, "打开"),
        EXPORT_FILE(124, "导出文件"),
        ADD_FILE_MEMBER(127, "添加文件成员"),
        MODIFY_FILE_MEMBER_PERMISSION(128, "修改文件成员权限"),
        REMOVE_FILE_MEMBER(129, "移除文件成员"),
        SET_DOCUMENT_WATERMARK(130, "设置文档水印"),
        MODIFY_INTERNAL_PERMISSION(131, "修改企业内权限"),
        MODIFY_EXTERNAL_PERMISSION(132, "修改企业外权限"),
        ADD_SHORTCUT_ENTRY(133, "添加快捷入口"),
        FORWARD_TO_WECHAT(134, "转发到微信"),
        PREVIEW(135, "预览"),
        PERMISSION_MANAGEMENT(136, "权限管理"),
        SECURITY_SETTINGS(139, "安全设置"),
        TRANSFER_DEPARTING_MEMBER_FILES(142, "离职成员文件转交"),
        APPROVE_DOWNLOAD_REQUEST(10001, "通过下载申请"),
        REJECT_DOWNLOAD_REQUEST(10002, "拒绝下载申请");

        private final int code;
        private final String description;

    FileSecurityOperation(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        // 可选：根据code获取对应的枚举实例
        public static FileSecurityOperation fromCode(int code) {
            for (FileSecurityOperation operation : FileSecurityOperation.values()) {
                if (operation.getCode() == code) {
                    return operation;
                }
            }
            throw new IllegalArgumentException("未知的操作代码: " + code);
        }


    /**
     * 将枚举FileSecurityOperation转换为List<SubType>
     * @return 包含所有枚举值的SubType列表
     */
    public static List<SubType> getSubTypeListFromEnum() {
        List<SubType> subTypeList = new ArrayList<>();
        // 遍历枚举常量数组
        for (FileSecurityOperation operation : FileSecurityOperation.values()) {
            // 将枚举的code和description映射到SubType的subCode和value
            SubType subType = SubType.builder()
                    .subCode(operation.getCode())
                    .value(operation.getDescription())
                    .build();
            subTypeList.add(subType);
        }
        return subTypeList;
    }


}
