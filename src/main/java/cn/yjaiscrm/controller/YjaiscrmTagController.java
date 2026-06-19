package cn.iyque.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.AiGenerateTagsRequest;
import cn.iyque.domain.AiGenerateTagsResponse;
import cn.iyque.domain.IYQueCustomerDto;
import cn.iyque.domain.IYQueGroupDto;
import cn.iyque.domain.ResponseResult;
import cn.iyque.entity.IYqueTag;
import cn.iyque.entity.IYqueTagGroup;
import cn.iyque.exception.IYqueException;
import cn.iyque.service.IYqueAiService;
import cn.iyque.service.IYqueTagGroupService;
import cn.iyque.service.IYqueTagService;
import cn.iyque.utils.TableSupport;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalTagGroupInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * 企微标签
 */
@RestController
@RequestMapping("/iYqueTag")
public class IYqueTagController{






    @Autowired
    private IYqueTagGroupService iYqueTagGroupService;


    @Autowired
    private IYqueTagService yqueTagService;


    @Autowired
    private IYqueAiService iYqueAiService;


    /**
     * 获取标签(客户标签)
     * @return
     */
    @GetMapping("/findIYqueTag")
    public ResponseResult findIYqueTag(Integer groupTagType){
        List<WxCpUserExternalTagGroupInfo.Tag> wxCpTags=new ArrayList<>();

        List<IYqueTag> iYqueTagList = yqueTagService.list(new LambdaQueryWrapper<IYqueTag>()
                .eq(IYqueTag::getTagType,null ==groupTagType?1:groupTagType));
        if(CollectionUtil.isNotEmpty(iYqueTagList)){

            iYqueTagList.stream().forEach(item->{

                WxCpUserExternalTagGroupInfo.Tag tag=new WxCpUserExternalTagGroupInfo.Tag();
                tag.setName(item.getName());
                tag.setId(item.getTagId());
                wxCpTags.add(tag);
            });

        }


        return new ResponseResult(wxCpTags);
    }


    /**
     * 同步标签
     * @return
     */
    @PostMapping("/synchTags")
    public ResponseResult synchTags(){
        try {
            iYqueTagGroupService.synchIYqueTag();
        }catch (IYqueException e){
            return new ResponseResult(HttpStatus.WE_ERROR,e.getMsg(),null);
        }
        return new ResponseResult("标签同步中,请稍后刷新查看");
    }


    /**
     * 删除标签组
     * @param ids
     * @return
     */
    @DeleteMapping("/{ids}")
    public ResponseResult remove(@PathVariable String[] ids)
    {

        try {
            iYqueTagGroupService.removeGroupTags(ids);
        }catch (IYqueException e){
            return new ResponseResult(HttpStatus.WE_ERROR,e.getMsg(),null);
        }
        return new ResponseResult();
    }


    /**
     * 新增标签组
     */
    @PostMapping
    public ResponseResult addTagGroup(@RequestBody IYqueTagGroup iYqueTagGroup)
    {


        try {
            iYqueTagGroupService.addTagGroup(iYqueTagGroup);
        }catch (IYqueException e){
            return new ResponseResult(HttpStatus.WE_ERROR,e.getMsg(),null);
        }
        return new ResponseResult();


    }

    /**
     * 修改标签组
     */
    @PutMapping
    public ResponseResult updateTagGroup(@RequestBody IYqueTagGroup iYqueTagGroup)
    {


        try {
            if(new Integer(2).equals(iYqueTagGroup.getGroupTagType())){//客群标签
                iYqueTagGroupService.updateCustomerGroupTagGroup(iYqueTagGroup);
            }else{//客户标签
                iYqueTagGroupService.updateTagGroup(iYqueTagGroup);
            }

        }catch (IYqueException e){
            return new ResponseResult(HttpStatus.WE_ERROR,e.getMsg(),null);
        }
        return new ResponseResult();

    }

    /**
     * 标签列表
     * @param yqueTagGroup
     * @return
     */
    @GetMapping("/findIYqueTagGroups")
    public ResponseResult findIYqueTagGroups(IYqueTagGroup yqueTagGroup){



        PageHelper.startPage(TableSupport.buildPageMybaitsRequest().getPageNum(), TableSupport.buildPageMybaitsRequest().getPageSize());
        List<IYqueTagGroup> list = iYqueTagGroupService.findIYqueTagGroups(yqueTagGroup);


        return new ResponseResult(list,new PageInfo(list).getTotal());
    }

    /**
     * AI生成标签
     * @param request
     * @return
     */
    @PostMapping("/generateTagsByAi")
    public ResponseResult generateTagsByAi(@RequestBody AiGenerateTagsRequest request){
        try {
            List<AiGenerateTagsResponse> result = iYqueAiService.generateTags(
                    request.getPrompt(),
                    request.getGroupCount(),
                    request.getTagCountPerGroup());
            return new ResponseResult(result);
        } catch (Exception e) {
            return new ResponseResult(HttpStatus.ERROR, "AI生成标签失败: " + e.getMessage(), null);
        }
    }





}
