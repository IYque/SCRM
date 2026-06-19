package cn.iyque.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.*;
import cn.iyque.entity.IYqueAnnexPeriod;
import cn.iyque.entity.IYqueMsgAnnex;
import cn.iyque.entity.IYqueShortLink;
import cn.iyque.service.IYqueAnnexPeriodService;
import cn.iyque.service.IYqueCustomerInfoService;
import cn.iyque.service.IYqueMsgAnnexService;
import cn.iyque.service.IYqueShortLinkService;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 获客链接
 */
@Slf4j
@RestController
@RequestMapping("/iyQue/shortLink")
public class IYqueShortLinkController {

    @Autowired
    private IYqueShortLinkService iYqueShortLinkService;

    @Autowired
    private IYqueMsgAnnexService iYqueMsgAnnexService;


    @Autowired
    private IYqueAnnexPeriodService iYqueAnnexPeriodService;


    @Autowired
    private IYqueCustomerInfoService iYqueCustomerInfoService;


    /**
     * 新增
     * @param iYqueShortLink
     * @return
     */
    @PostMapping("/save")
    public ResponseResult save(@RequestBody IYqueShortLink iYqueShortLink) {
        try {
            iYqueShortLinkService.save(iYqueShortLink);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }


    /**
     * 更新
     * @param iYqueShortLink
     * @return
     */
    @PutMapping("/update")
    public ResponseResult update(@RequestBody IYqueShortLink iYqueShortLink){
        try {
            iYqueShortLinkService.update(iYqueShortLink);
        }catch (Exception e){
            return new ResponseResult(HttpStatus.ERROR,e.getMessage(),null);
        }

        return new ResponseResult();
    }



    /**
     * 获取列表
     * @return
     */
    @GetMapping("/findIYqueShortLink")
    public ResponseResult<IYqueShortLink> findIYqueShortLink(
            IYqueShortLink iYqueShortLink){
        Page<IYqueShortLink> iYqueShortLinks = iYqueShortLinkService.findAll(
                iYqueShortLink,
                PageRequest.of(TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(),
                        Sort.by("updateTime").descending()));
        return new ResponseResult(iYqueShortLinks.getContent(),iYqueShortLinks.getTotalElements());
    }


    /**
     * 获取所有活码id与名称
     * @return
     */
    @GetMapping("/findIYqueUserCodeKvs")
    public ResponseResult<List<IYqueKvalStrVo>>  findIYqueUserCodeKvs(){

        List<IYqueKvalStrVo> iYqueUserCodeKvs = iYqueShortLinkService.findIYqueShorkLinkKvs();


        return new ResponseResult<>(iYqueUserCodeKvs);


    }






    /**
     * 通过id批量删除
     *
     * @param ids id列表
     * @return 结果
     */
    @DeleteMapping(path = "/{ids}")
    public ResponseResult batchDelete(@PathVariable("ids") Long[] ids) {

        iYqueShortLinkService.batchDelete(ids);

        return new ResponseResult();
    }


    /**
     * 获取附件
     * @param id
     * @return
     */
    @GetMapping("/findIYqueMsgAnnexByMsgId/{id}")
    public ResponseResult<IYqueMsgAnnex> findIYqueMsgAnnexByMsgId(@PathVariable Long id){

        List<IYqueMsgAnnex> iYqueMsgAnnexes = iYqueMsgAnnexService.findIYqueMsgAnnexByMsgId(id);


        return  new ResponseResult(
                CollectionUtil.isNotEmpty(iYqueMsgAnnexes)?iYqueMsgAnnexes:new ArrayList<>()
        );
    }


    /**
     * 获取时段欢迎语附件
     * @param id
     * @return
     */
    @GetMapping("/findIYqueMsgPeriodAnnexByMsgId/{id}")
    public ResponseResult<IYqueAnnexPeriod> findIYqueMsgPeriodAnnexByMsgId(@PathVariable Long id){

        List<IYqueAnnexPeriod> iYqueAnnexPeriods = iYqueAnnexPeriodService.findIYqueAnnexPeriodByMsgId(id);

        return  new ResponseResult(
                iYqueAnnexPeriods
        );
    }


    /**
     * 统计tab
     * @return
     */
    @GetMapping("/countTotalTab")
    public ResponseResult<IYqueUserCodeCountVo> countTotalTab(IYQueCountQuery queCountQuery){
        IYqueUserCodeCountVo iYqueUserCodeCountVo = iYqueCustomerInfoService.countTotalTab(queCountQuery,false);

        return new ResponseResult<>(iYqueUserCodeCountVo);
    }


    /**
     * 统计趋势图
     * @param queCountQuery
     * @return
     */
    @GetMapping("/countTrend")
    public ResponseResult<IYQueTrendCount> countTrend(IYQueCountQuery queCountQuery){

        IYQueTrendCount trendCount = iYqueCustomerInfoService.countTrend(queCountQuery,false);

        return new ResponseResult<>(trendCount);
    }


    /**
     * 同步获客外链
     * @return
     */
    @PostMapping("/synchShortLink")
    public ResponseResult synchShortLink(){

        iYqueShortLinkService.synchShortLink();

        return new ResponseResult("获客外链同步中,请稍后查看");
    }

    /**
     * 同步指定的获客外链
     * @param linkIds 获客外链ID列表，用逗号分隔
     * @return
     */
    @PostMapping("/synchShortLinkByLinkIds")
    public ResponseResult synchShortLinkByLinkIds(@RequestParam String linkIds){
        if (StrUtil.isEmpty(linkIds)) {
            return new ResponseResult(HttpStatus.ERROR, "获客外链ID不能为空", null);
        }

        List<String> linkIdList = Arrays.asList(linkIds.split(","));
        iYqueShortLinkService.synchShortLinkByLinkIds(linkIdList);

        return new ResponseResult("指定获客外链同步中,请稍后查看");
    }

    /**
     * 修复configId为空的获客外链记录
     * @return
     */
    @PostMapping("/fixEmptyConfigIds")
    public ResponseResult fixEmptyConfigIds(){
        try {
            iYqueShortLinkService.fixEmptyConfigIds();
            return new ResponseResult("configId修复完成");
        } catch (Exception e) {
            return new ResponseResult(HttpStatus.ERROR, "修复失败: " + e.getMessage(), null);
        }
    }

    /**
     * 清理重复的获客外链记录
     * @return
     */
    @PostMapping("/cleanDuplicateShortLinks")
    public ResponseResult cleanDuplicateShortLinks(){
        try {
            Map<String, Object> result = iYqueShortLinkService.cleanDuplicateShortLinks();
            return new ResponseResult(result);
        } catch (Exception e) {
            return new ResponseResult(HttpStatus.ERROR, "清理失败: " + e.getMessage(), null);
        }
    }

    /**
     * 检查configId为空的记录数量
     * @return
     */
    @GetMapping("/checkEmptyConfigIds")
    public ResponseResult checkEmptyConfigIds(){
        try {
            List<IYqueShortLink> allLinks = iYqueShortLinkService.findAll(Pageable.unpaged()).getContent();
            long emptyConfigIdCount = allLinks.stream()
                    .filter(link -> StrUtil.isEmpty(link.getConfigId()))
                    .count();

            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", allLinks.size());
            result.put("emptyConfigIdCount", emptyConfigIdCount);
            result.put("validConfigIdCount", allLinks.size() - emptyConfigIdCount);

            return new ResponseResult(result);
        } catch (Exception e) {
            return new ResponseResult(HttpStatus.ERROR, "检查失败: " + e.getMessage(), null);
        }
    }

    /**
     * 测试企业微信API调用
     * @return
     */
    @GetMapping("/testWxApi")
    public ResponseResult testWxApi(){
        try {
            Map<String, Object> result = iYqueShortLinkService.testWxApi();
            return new ResponseResult(result);
        } catch (Exception e) {
            log.error("测试API调用失败: {}", e.getMessage(), e);
            return new ResponseResult(HttpStatus.ERROR, "测试失败: " + e.getMessage(), null);
        }
    }



    /**
     * 获取所有获客外链的configId列表
     * @return
     */
    @GetMapping("/getShortLinkConfigIds")
    public ResponseResult getShortLinkConfigIds(){
        try {
            List<IYqueKvalStrVo> configIds = iYqueShortLinkService.getShortLinkConfigIds();
            return new ResponseResult(configIds);
        } catch (Exception e) {
            log.error("获取获客外链配置ID列表失败: {}", e.getMessage(), e);
            return new ResponseResult(HttpStatus.ERROR, "获取配置ID列表失败: " + e.getMessage(), null);
        }
    }

    /**
     * 获取获客链接的客户列表
     * @param linkId 获客链接ID
     * @param limit 返回的最大记录数，最大值1000
     * @param cursor 用于分页查询的游标
     * @param customerName 客户姓名（可选，用于搜索）
     * @param followUser 跟进人姓名（可选，用于筛选）
     * @param startDate 开始日期（可选，用于时间范围筛选）
     * @param endDate 结束日期（可选，用于时间范围筛选）
     * @return
     */
    @GetMapping("/getCustomerList")
    public ResponseResult getCustomerList(@RequestParam String linkId,
                                          @RequestParam(defaultValue = "100") Integer limit,
                                          @RequestParam(required = false) String cursor,
                                          @RequestParam(required = false) String customerName,
                                          @RequestParam(required = false) String followUser,
                                          @RequestParam(required = false) String startDate,
                                          @RequestParam(required = false) String endDate) {
        try {
            log.info("获取客户列表请求 - linkId: {}, limit: {}, cursor: {}, customerName: {}, followUser: {}, startDate: {}, endDate: {}",
                    linkId, limit, cursor, customerName, followUser, startDate, endDate);

            Object result = iYqueShortLinkService.getCustomerList(linkId, limit, cursor, customerName, followUser, startDate, endDate);
            log.info("获取客户列表成功");
            return new ResponseResult(result);
        } catch (Exception e) {
            log.error("获取客户列表失败: {}", e.getMessage(), e);
            return new ResponseResult(HttpStatus.ERROR, e.getMessage(), null);
        }
    }


}
