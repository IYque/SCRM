package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.iyque.dao.IYqueAnalysisHotWordDao;
import cn.iyque.domain.IYqueAnalysisHotWordTabVo;
import cn.iyque.domain.IYqueAnalysisHotWordVo;
import cn.iyque.entity.IYqueAnalysisHotWord;
import cn.iyque.service.IYqueAnalysisHotWordService;
import cn.iyque.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class IYqueAnalysisHotWordServiceImpl implements IYqueAnalysisHotWordService {

    @Autowired
    private IYqueAnalysisHotWordDao yqueAnalysisHotWordDao;

    @Override
    public Page<IYqueAnalysisHotWord> findAll(IYqueAnalysisHotWord iYqueAnalysisHotWord, Pageable pageable) {


        Specification<IYqueAnalysisHotWord> spec = Specification.where(null);

        if(iYqueAnalysisHotWord.getCategoryId() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("categoryId"), iYqueAnalysisHotWord.getCategoryId()));
        }

        if(iYqueAnalysisHotWord.getHotWordId() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("hotWordId"), iYqueAnalysisHotWord.getHotWordId()));
        }

        //按照时间查询
        if (iYqueAnalysisHotWord.getStartTime() != null && iYqueAnalysisHotWord.getEndTime() != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("msgTime"), DateUtils.setTimeToStartOfDay( iYqueAnalysisHotWord.getStartTime()), DateUtils.setTimeToEndOfDay( iYqueAnalysisHotWord.getEndTime())));
        }



        if(StringUtils.isNotEmpty(iYqueAnalysisHotWord.getHotWordIds())){
            List<Long> hotWordIdList = Arrays.stream(iYqueAnalysisHotWord.getHotWordIds().split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            spec = spec.and((root, query, cb) -> root.get("hotWordId").in(hotWordIdList));
        }



        return yqueAnalysisHotWordDao.findAll(spec,pageable);
    }

    @Override
    public List<IYqueAnalysisHotWordVo> hotWordTop5(IYqueAnalysisHotWord iYqueAnalysisHotWord) {
        List<IYqueAnalysisHotWordVo> hotWordVos=new ArrayList<>();

        Specification<IYqueAnalysisHotWord> spec = Specification.where(null);
        if (iYqueAnalysisHotWord.getStartTime() != null && iYqueAnalysisHotWord.getEndTime() != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("msgTime"), DateUtils.setTimeToStartOfDay( iYqueAnalysisHotWord.getStartTime()), DateUtils.setTimeToEndOfDay( iYqueAnalysisHotWord.getEndTime())));
        }

        List<IYqueAnalysisHotWord> hotWords = yqueAnalysisHotWordDao.findAll(spec);
        if(CollectionUtil.isNotEmpty(hotWords)){



          hotWords.stream()
                    .collect(Collectors.groupingBy(IYqueAnalysisHotWord::getHotWordName))
                  .forEach((k,v)->{

                      hotWordVos.add(
                              IYqueAnalysisHotWordVo.builder()
                                      .hotWord(k)
                                      .hotWordDiscussNumber(v.size())
                                      .build()
                      );

                  });

        }

        return hotWordVos.stream()
                // 按hotWordDiscussNumber降序排序
                .sorted(Comparator.comparingLong(IYqueAnalysisHotWordVo::getHotWordDiscussNumber).reversed())
                // 提取前五个元素
                .limit(5)
                // 收集为List
                .collect(Collectors.toList());
    }

    @Override
    public List<IYqueAnalysisHotWordVo> hotWordCategoryTop5(IYqueAnalysisHotWord iYqueAnalysisHotWord) {
        List<IYqueAnalysisHotWordVo> hotWordVos=new ArrayList<>();

        Specification<IYqueAnalysisHotWord> spec = Specification.where(null);
        if (iYqueAnalysisHotWord.getStartTime() != null && iYqueAnalysisHotWord.getEndTime() != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("msgTime"), DateUtils.setTimeToStartOfDay( iYqueAnalysisHotWord.getStartTime()), DateUtils.setTimeToEndOfDay( iYqueAnalysisHotWord.getEndTime())));
        }

        if(iYqueAnalysisHotWord.getCategoryId() != null){
            spec = spec.and((root, query, cb) -> cb.equal(root.get("categoryId"), iYqueAnalysisHotWord.getCategoryId()));
        }

        List<IYqueAnalysisHotWord> hotWords = yqueAnalysisHotWordDao.findAll(spec);
        if(CollectionUtil.isNotEmpty(hotWords)){



            hotWords.stream()
                    .collect(Collectors.groupingBy(IYqueAnalysisHotWord::getCategoryName))
                    .forEach((k,v)->{

                        hotWordVos.add(
                                IYqueAnalysisHotWordVo.builder()
                                        .hotWord(k)
                                        .hotWordDiscussNumber(v.size())
                                        .build()
                        );

                    });

        }

        return hotWordVos.stream()
                // 按hotWordDiscussNumber降序排序
                .sorted(Comparator.comparingLong(IYqueAnalysisHotWordVo::getHotWordDiscussNumber).reversed())
                // 提取前五个元素
                .limit(5)
                // 收集为List
                .collect(Collectors.toList());
    }

    @Override
    public IYqueAnalysisHotWordTabVo findHotWordTab() {
        IYqueAnalysisHotWordTabVo analysisHotWordTabVo=new IYqueAnalysisHotWordTabVo();
        analysisHotWordTabVo.setDiscussTotalNumber(
                yqueAnalysisHotWordDao.count()
        );



        return analysisHotWordTabVo;
    }

}
