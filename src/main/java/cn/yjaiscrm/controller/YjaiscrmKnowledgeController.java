package cn.iyque.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.chain.vectorstore.IYqueVectorStore;
import cn.iyque.domain.KnowledgeInfoUploadRequest;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueKnowledgeAttach;
import cn.iyque.entity.IYqueKnowledgeFragment;
import cn.iyque.entity.IYqueKnowledgeInfo;
import cn.iyque.service.*;
import cn.iyque.utils.TableSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 知识库
 */
@RequestMapping("/knowledge")
@Slf4j
@RestController
public class IYqueKnowledgeController {

    @Autowired
    private IYqueKnowledgeInfoService iYqueKnowledgeInfoService;

    @Autowired
    private IYqueKnowledgeAttachService yqueKnowledgeAttachService;


    @Autowired
    private IYqueKnowledgeFragmentService yqueKnowledgeFragmentService;









    /**
     * 获取知识库列表
     * @param knowledgeInfo
     * @return
     */
    @GetMapping("/findKnowledgeByPage")
    public ResponseResult<IYqueKnowledgeInfo> findKnowledgeByPage(IYqueKnowledgeInfo knowledgeInfo){

        Page<IYqueKnowledgeInfo> iYqueKnowledgeInfos = iYqueKnowledgeInfoService.findAll(knowledgeInfo,
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));
        return new ResponseResult(iYqueKnowledgeInfos.getContent(),iYqueKnowledgeInfos.getTotalElements());
    }


    /**
     * 获取所有知识库
     * @return
     */
    @GetMapping("/findAll")
    public ResponseResult<List<IYqueKnowledgeInfo>> findAll(){
       List<IYqueKnowledgeInfo> infoList=iYqueKnowledgeInfoService.findAll();

       return new ResponseResult<>(infoList);

    }



    /**
     * 新增知识库
     */
    @PostMapping("/save")
    public ResponseResult save(@RequestBody IYqueKnowledgeInfo knowledgeInfo) {
        knowledgeInfo.setCreateTime(new Date());
        iYqueKnowledgeInfoService.saveOrUpdate(knowledgeInfo);
        return new ResponseResult();
    }

    /**
     * 上传知识库附件
     */
    @PostMapping(value = "/attach/upload")
    public ResponseResult upload(KnowledgeInfoUploadRequest request){
        iYqueKnowledgeInfoService.upload(request);
        return new ResponseResult();
    }


    /**
     * 删除知识库
     */
    @DeleteMapping("/remove/{id}")
    public ResponseResult remove(@PathVariable Long id){
        iYqueKnowledgeInfoService.removeKnowledge(id);
        return new ResponseResult();
    }


    /**
     * 查询知识附件信息列表
     */
    @GetMapping("/detail/{kid}")
    public  ResponseResult<IYqueKnowledgeAttach> attach(@PathVariable Long kid){
        Page<IYqueKnowledgeAttach> iYqueKnowledgeInfos = yqueKnowledgeAttachService.findAll(IYqueKnowledgeAttach.builder()
                        .kid(kid)
                        .build(),
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));
        return new ResponseResult(iYqueKnowledgeInfos.getContent(),iYqueKnowledgeInfos.getTotalElements());
    }




    /**
     * 删除知识库附件
     *
     */
    @DeleteMapping("/attach/remove/{docId}")
    public ResponseResult removeAttach(@PathVariable Long docId) {
        yqueKnowledgeAttachService.removeKnowledgeAttach(docId);
        return new ResponseResult();
    }


    /**
     * 查询知识片段
     */
    @GetMapping("/fragment/list/{docId}")
    public ResponseResult<IYqueKnowledgeFragment> fragmentList(@PathVariable Long docId) {
        Page<IYqueKnowledgeFragment> iYqueKnowledgeFragments = yqueKnowledgeFragmentService.findAll(IYqueKnowledgeFragment.builder()
                        .docId(docId)
                        .build(),
                PageRequest.of( TableSupport.buildPageRequest().getPageNum(),
                        TableSupport.buildPageRequest().getPageSize(), Sort.by("createTime").descending()));
        return new ResponseResult(iYqueKnowledgeFragments.getContent(),iYqueKnowledgeFragments.getTotalElements());
    }




}
