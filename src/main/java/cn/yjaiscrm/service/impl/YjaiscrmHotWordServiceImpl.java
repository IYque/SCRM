package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import cn.iyque.constant.IYqueContant;
import cn.iyque.dao.IYqueAnalysisHotWordDao;
import cn.iyque.dao.IYqueHotWordDao;
import cn.iyque.entity.*;
import cn.iyque.service.IYqueAiService;
import cn.iyque.service.IYqueCategoryService;
import cn.iyque.service.IYqueHotWordService;
import cn.iyque.service.IYqueMsgAuditService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class IYqueHotWordServiceImpl implements IYqueHotWordService {

    @Autowired
    private IYqueHotWordDao iYqueHotWordDao;


    @Autowired
    private IYqueMsgAuditService iYqueMsgAuditService;

    @Autowired
    private IYqueAnalysisHotWordDao yqueAnalysisHotWordDao;


    @Autowired
    private IYqueCategoryService categoryService;



    @Autowired
    private IYqueAiService aiService;




    private final String aiHotWordAnalysisTpl = "任务描述：\n" +
            "逐条分析以下聊天内容:" +
            "%s\n" +
            "判断是否包含相关热词,与热词相近词相同的语义：\n" +
            "%s\n\n" +
            "分析要求：\n" +
            "1.逐条分析聊天内容(其中content表示具体的聊天内容),判断是否包含热词以及热词下相关词的语义（hotWord表示热词,nearHotWord表示热词相似词多个使用逗号隔开）。\n" +
            "2.如何聊天内容未与相关热词语义匹配上,则该内容不做输出。\n" +
            "3.最终输出结果必须严格按照以下JSON格式结构化输出,不可包含任何无关格式与内容：\n" +
            "[{\"msgId\":\"消息的id\", \"fromId\":\"发送人的id\", \"fromName\":\"发送人名称\", \"acceptId\":\"接收人id\", \"acceptName\":\"接收人名称\", \"content\":\"会话内容\", \"msgTime\":\"消息发送时间,以Date类型输出\", \"hotWordId\":\"热词id\", \"hotWordName\":\"热词名称\", \"categoryId\":\"分类id\", \"categoryName\":\"分类名称\"}]";




    @Override
    public Page<IYqueHotWord> findAll(IYqueHotWord iYqueHotWord, Pageable pageable) {

        Specification<IYqueHotWord> spec = Specification.where(null);

        if(StringUtils.isNotEmpty(iYqueHotWord.getHotWord())){
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("hotWord")), "%" + iYqueHotWord.getHotWord().toLowerCase() + "%"));
        }

        if(iYqueHotWord.getCategoryId() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("categoryId"), iYqueHotWord.getCategoryId()));
        }


        return iYqueHotWordDao.findAll(spec,pageable);
    }

    @Override
    public void saveOrUpdate(IYqueHotWord iYqueHotWord) {


        if(iYqueHotWord.getId() != null){
            iYqueHotWord.setUpdateTime(new Date());
        }else{
            iYqueHotWord.setCreateTime(new Date());
            iYqueHotWord.setUpdateTime(new Date());
        }
        iYqueHotWordDao.saveAndFlush(iYqueHotWord);

    }

    @Override
    public List<IYqueHotWord> findAll() {
        return iYqueHotWordDao.findAll();
    }





    @Override
    public void batchDelete(Long[] ids) {

        List<IYqueHotWord> iYqueHotWords = iYqueHotWordDao.findAllById(Arrays.asList(ids));

        if(CollectionUtil.isNotEmpty(iYqueHotWords)){
            iYqueHotWords.stream().forEach(k->{
                k.setDelFlag(IYqueContant.DEL_STATE);

                try {
                    iYqueHotWordDao.saveAndFlush(k);

                }catch (Exception e){
                    log.error("热词删除失败:"+e.getMessage());
                }

            });

        }


    }

    @Override
    public String aiHotWordAnalysis(List<IYqueHotWord> iYqueHotWords, BaseEntity baseEntity) {
        if(CollectionUtil.isNotEmpty(iYqueHotWords)){

            String customerMsgData = iYqueMsgAuditService.findByMsgTimeBetweenAndAcceptType(baseEntity.getStartTime(),baseEntity.getEndTime(),baseEntity.getMsgAuditType());

            if(StringUtils.isNotEmpty(customerMsgData)){


                try {

                    String prompt = String.format(aiHotWordAnalysisTpl,customerMsgData, JSONUtil.toJsonStr(iYqueHotWords));

                    log.info("当前热词分析提示词:"+prompt);

                    String result = aiService.aiHandleCommonContent(prompt);

                    log.info("大模型输出原生结果:"+result);

                    if(StringUtils.isNotEmpty(result)){
                        // 清理字符串：去除 ```json 和换行符
                        String cleanJsonString = result
                                .replace("```json", "")
                                .replace("```", "")
                                .trim();

                        if(StringUtils.isNotEmpty(cleanJsonString)){
                            List<IYqueAnalysisHotWord> analysisHotWords
                                    = JSONUtil.toList(cleanJsonString, IYqueAnalysisHotWord.class);

                            if(CollectionUtil.isNotEmpty(analysisHotWords)){
                                List<IYqueCategory> iYqueCategories = categoryService.findAll(new IYqueCategory());

                                analysisHotWords.stream().forEach(k->{
                                    k.setAcceptType(
                                            baseEntity.getMsgAuditType()
                                    );
                                    if(k.getCategoryId() !=null && CollectionUtil.isNotEmpty(iYqueCategories)){
                                        k.setCategoryName(
                                                iYqueCategories.stream().filter(item->item.getId().equals(k.getCategoryId())).findFirst().get().getName()
                                        );
                                    }
                                    k.setAnalysisTime(new Date());
                                });


                                yqueAnalysisHotWordDao.deleteByMsgIds(
                                        analysisHotWords.stream()
                                                .map(IYqueAnalysisHotWord::getMsgId)
                                                .collect(Collectors.toList())
                                );

                                yqueAnalysisHotWordDao.saveAll(analysisHotWords);


                            }


                        }


                    }

                }catch (Exception e){

                    return new String(e.getMessage());
                }






            }else{

                return new String("当前时间段内不存在会话内容");
            }

        }

        return new String("当前ai热词分析中,请稍后查看");

    }

}
