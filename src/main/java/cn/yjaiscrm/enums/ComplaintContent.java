package cn.iyque.enums;

import cn.hutool.core.collection.ListUtil;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
@Getter
public enum ComplaintContent {

   A(1,"发布不适当内容对我造成骚扰", ListUtil.toList(
           SubType.builder()
                   .subCode(100)
                   .value("色情")
                   .build(),
           SubType.builder()
                   .subCode(101)
                   .value("违法犯罪及违禁品")
                   .build(),
           SubType.builder()
                   .subCode(102)
                   .value("赌博")
                   .build(),
           SubType.builder()
                   .subCode(103)
                   .value("政治谣言")
                   .build(),
           SubType.builder()
                   .subCode(104)
                   .value("暴恐血腥")
                   .build(),
           SubType.builder()
                   .subCode(105)
                   .value("自杀自残")
                   .build(),
           SubType.builder()
                   .subCode(106)
                   .value("其他违规内容")
                   .build()
   )),
   B(2,"存在欺诈骗钱行为",ListUtil.toList(
           SubType.builder()
                   .subCode(200)
                   .value("金融诈骗(贷款/提额/代开/套现等)")
                   .build(),
           SubType.builder()
                   .subCode(201)
                   .value("网络兼职刷单诈骗")
                   .build(),
           SubType.builder()
                   .subCode(202)
                   .value("返利诈骗")
                   .build(),
           SubType.builder()
                   .subCode(203)
                   .value("网络交友诈骗")
                   .build(),
           SubType.builder()
                   .subCode(204)
                   .value("虚假投资理财诈骗")
                   .build(),
           SubType.builder()
                   .subCode(205)
                   .value("赌博诈骗")
                   .build(),
           SubType.builder()
                   .subCode(206)
                   .value("收款不发货")
                   .build(),
           SubType.builder()
                   .subCode(207)
                   .value("仿冒他人诈骗")
                   .build(),
           SubType.builder()
                   .subCode(208)
                   .value("免费送诈骗")
                   .build(),
           SubType.builder()
                   .subCode(209)
                   .value("游戏相关诈骗(代练/充值等)")
                   .build(),
           SubType.builder()
                   .subCode(210)
                   .value("其他诈骗行为")
                   .build()
   )),
   C(3,"此账号可能被盗用",new ArrayList<>()),
   D(4,"存在侵权行为(侵犯知识产权、人身权)",new ArrayList<>()),
   E(5,"发布仿冒品信息",new ArrayList<>()),
   F(6,"冒充他人",new ArrayList<>()),
   G(7,"侵犯未成年人权益",new ArrayList<>()),
   H(8,"粉丝无底线追星行为",new ArrayList<>()),
    I(9,"其他",new ArrayList<>());

    private Integer code;

    private String value;


    private List<SubType> subTypeList;

    ComplaintContent(Integer code, String value, List<SubType> subTypeList){
        this.code=code;
        this.value=value;
        this.subTypeList=subTypeList;
    }
    @Data
    @Builder
    public static class SubType {
        private Integer subCode;
        private String value;

    }

    /**
     * 获取所有一级类型及其子类型的信息
     * @return 包含所有一级类型及其子类型的列表
     */
    public static List<TypeWithSubTypes> getAllComplaintContent() {
        List<TypeWithSubTypes> result = new ArrayList<>();
        for (ComplaintContent type : ComplaintContent.values()) {
            if (!type.getSubTypeList().isEmpty()) {
                result.add(TypeWithSubTypes.builder()
                        .code(type.getCode())
                        .value(type.getValue())
                        .subTypes(type.getSubTypeList())
                        .build());
            } else {
                result.add(TypeWithSubTypes.builder()
                        .code(type.getCode())
                        .value(type.getValue())
                        .subTypes(new ArrayList<>())
                        .build());
            }
        }
        return result;
    }

    // 静态映射
    private static final Map<Integer, ComplaintContent> codeToComplaintContentMap = Arrays.stream(values())
            .collect(Collectors.toMap(ComplaintContent::getCode, type -> type));

    // 静态映射：子代码 -> SubType
    private static final Map<Integer, SubType> codeToSubTypeMap = Arrays.stream(values())
            .flatMap(type -> type.getSubTypeList().stream()
                    .map(subType -> new AbstractMap.SimpleEntry<>(subType.getSubCode(), subType)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


    /**
     * 根据传入的 code 查询对应的 value。
     * 如果 code 是顶级代码，则返回顶级类型的 value。
     * 如果 code 是子代码（如 101），则返回对应子类型的 value。
     *
     * @param code 要查询的代码
     * @return 对应的 value，如果不存在则返回 null
     */
    public static String getValueByCode(Integer code) {
        if (code == null) {
            return I.value;
        }

        // 先检查是否是子类型
        SubType subType = codeToSubTypeMap.get(code);
        if (subType != null) {
            return subType.getValue();
        }

        // 再检查是否是顶级类型
        ComplaintContent complaintContent = codeToComplaintContentMap.get(code);
        if (complaintContent != null) {
            return complaintContent.getValue();
        }

        return I.value;
    }


    /**
     * 数据传输对象，用于表示类型及其子类型的信息
     */
    @Data
    @Builder
    public static class TypeWithSubTypes {
        private final Integer code;
        private final String value;
        private final List<ComplaintContent.SubType> subTypes;
    }






}
