package cn.iyque.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import cn.iyque.constant.IYqueConstant;
import cn.iyque.domain.IYQueGroupDto;
import cn.iyque.entity.IYqueTag;
import cn.iyque.entity.IYqueTagGroup;
import cn.iyque.exception.IYqueException;
import cn.iyque.mapper.IYqueTagGroupMapper;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueTagGroupService;
import cn.iyque.service.IYqueTagService;
import cn.iyque.utils.SnowFlakeUtils;
import cn.iyque.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.WxCpBaseResp;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalTagGroupInfo;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalTagGroupList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
public class IYqueTagGroupServiceImpl extends ServiceImpl<IYqueTagGroupMapper, IYqueTagGroup> implements IYqueTagGroupService {


    @Autowired
    private IYqueConfigService yqueConfigService;

    @Autowired
    private IYqueTagService yqueTagService;

    @Autowired
    private TransactionTemplate transactionTemplate;


    @Override
    @Async
    public void synchIYqueTag() throws IYqueException {

        transactionTemplate.execute(status -> {
            try {
                List<IYqueTagGroup> tagGroups=new ArrayList<>();
                List<IYqueTag> tagList=new ArrayList<>();

                WxCpUserExternalTagGroupList corpTagList = yqueConfigService.findWxcpservice()
                        .getExternalContactService()
                        .getCorpTagList(null);
                if(corpTagList.success()){
                    List<WxCpUserExternalTagGroupList.TagGroup>
                            tagGroupList = corpTagList.getTagGroupList();
                    if(CollectionUtil.isNotEmpty(tagGroupList)){
                        tagGroupList.stream().forEach(k->{
                            //构建标签组
                            IYqueTagGroup iYqueTagGroup = IYqueTagGroup.builder()
                                    .groupId(k.getGroupId())
                                    .orderNumber(k.getOrder())
                                    .groupTagType(1)
                                    .groupName(k.getGroupName())
                                    .delFlag(IYqueConstant.commonState)
                                    .build();

                            List<WxCpUserExternalTagGroupList.TagGroup.Tag>
                                    tags = k.getTag();
                            if(CollectionUtil.isNotEmpty(tags)){
                                tags.stream().forEach(kk->{
                                    tagList.add(
                                            IYqueTag.builder().tagId(kk.getId())
                                                    .groupId(k.getGroupId())
                                                    .orderNumber(kk.getOrder())
                                                    .tagType(1)
                                                    .name(kk.getName())
                                                    .delFlag(IYqueConstant.commonState)
                                                    .build()
                                    );
                                });
                            }
                            tagGroups.add(iYqueTagGroup);
                        });

                    }
                }

                //移除不存在的标签
                this.remove(new LambdaQueryWrapper<IYqueTagGroup>()
                        .notIn(CollectionUtil.isNotEmpty(tagGroups),IYqueTagGroup::getGroupId,tagGroups.stream().map(IYqueTagGroup::getGroupId).collect(Collectors.toList())));

                //移除不存在的标签组
                yqueTagService.remove(new LambdaQueryWrapper<IYqueTag>()
                        .notIn(CollectionUtil.isNotEmpty(tagList),IYqueTag::getTagId,
                                tagList.stream().map(IYqueTag::getTagId).collect(Collectors.toList())));


                if(CollectionUtil.isNotEmpty(tagGroups) && CollectionUtil.isNotEmpty(tagList)){
                    //查询出数据库中已存的标签组
                    List<IYqueTagGroup> iYqueTagGroups = this.list();
                    if(CollectionUtil.isNotEmpty(iYqueTagGroups)){
                        tagGroups.stream().forEach(tagGroup->{
                            Optional<IYqueTagGroup> optional = iYqueTagGroups.stream().filter(item -> item.getGroupId().equals(tagGroup.getGroupId())).findAny();
                            if(optional.isPresent()){
                                tagGroup.setId(
                                        optional.get().getId()
                                );
                            }
                        });
                    }

                    //查询出数据库中已存的标签
                    List<IYqueTag> iYqueTags = yqueTagService.list();
                    if(CollectionUtil.isNotEmpty(iYqueTags)){
                        tagList.stream().forEach(tag->{

                            Optional<IYqueTag> tagOptional = iYqueTags.stream()
                                    .filter(item -> item.getTagId().equals(tag.getTagId()) && item.getGroupId().equals(tag.getGroupId()))
                                    .findAny();
                            if(tagOptional.isPresent()){
                                tag.setId(
                                        tagOptional.get().getId()
                                );
                            }
                        });
                    }

                    this.saveOrUpdateBatch(tagGroups);

                    yqueTagService.saveOrUpdateBatch(tagList);

                }

                return null;
            }catch (Exception e){
                log.error("标签同步失败:"+e.getMessage());
                // 手动标记事务回滚（可选，若未捕获异常则自动回滚）
                status.setRollbackOnly();
                throw new IYqueException(e.getMessage()); // 必须重新抛出异常，否则事务不会回滚
            }
        });




    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTagGroup(IYqueTagGroup tagGroup) {
        try {

            if(StringUtils.isNotEmpty(tagGroup.getGroupName())&&CollectionUtil.isNotEmpty(tagGroup.getWeTags())){



                if(new Integer(2).equals(tagGroup.getGroupTagType())){ //客群标签(无需同步企业微信)
                    tagGroup.setGroupTagType(2);
                    tagGroup.setOrderNumber(0L);
                    tagGroup.setGroupId(SnowFlakeUtils.nextId().toString());
                    tagGroup.setDelFlag(IYqueConstant.commonState);

                    if(this.save(tagGroup)) {
                        tagGroup.getWeTags().stream().forEach(item -> {
                            item.setDelFlag(IYqueConstant.commonState);
                            item.setOrderNumber(0L);
                            item.setTagType(2);
                            item.setGroupId(tagGroup.getGroupId());
                            item.setTagId(SnowFlakeUtils.nextId().toString());
                        });
                        yqueTagService.saveOrUpdateBatch(tagGroup.getWeTags());
                    }

                }else{ //客户标签(同步企业微信标签)

                    WxCpUserExternalTagGroupInfo groupInfo=new WxCpUserExternalTagGroupInfo();
                    WxCpUserExternalTagGroupInfo.TagGroup wxTagGroup=new WxCpUserExternalTagGroupInfo.TagGroup();
                    wxTagGroup.setGroupName(tagGroup.getGroupName());

                    List<WxCpUserExternalTagGroupInfo.Tag> tags=new ArrayList<>();
                    tagGroup.getWeTags().stream().forEach(item->{
                        WxCpUserExternalTagGroupInfo.Tag wxTag=new WxCpUserExternalTagGroupInfo.Tag();
                        wxTag.setName(item.getName());
                        tags.add(wxTag);
                    });
                    wxTagGroup.setTag(tags);
                    groupInfo.setTagGroup(wxTagGroup);


                    WxCpUserExternalTagGroupInfo wxGroupInfo = yqueConfigService.findWxcpservice().
                            getExternalContactService().addCorpTag(groupInfo);
                    if(wxGroupInfo.success()){
                        tagGroup.setGroupTagType(1);
                        tagGroup.setOrderNumber(0L);
                        tagGroup.setGroupId(wxGroupInfo.getTagGroup().getGroupId());
                        tagGroup.setDelFlag(IYqueConstant.commonState);
                        if(this.save(tagGroup)) {
                            tagGroup.getWeTags().stream().forEach(item -> {
                                item.setDelFlag(IYqueConstant.commonState);
                                item.setOrderNumber(0L);
                                item.setTagType(1);
                                item.setGroupId(tagGroup.getGroupId());
                                Optional<WxCpUserExternalTagGroupInfo.Tag> optionalTag =
                                        wxGroupInfo.getTagGroup().getTag().stream()
                                                .filter(kk -> kk.getName().equals(item.getName())).findAny();
                                if (optionalTag.isPresent()) {
                                    item.setTagId(optionalTag.get().getId());
                                }
                            });
                            yqueTagService.saveOrUpdateBatch(tagGroup.getWeTags());
                        }
                    }




                }

            }


        }catch (Exception e){
            log.error("新增标签失败:"+e.getMessage());
             throw new IYqueException(e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTagGroup(IYqueTagGroup tagGroup) {


        try {

            WxCpService wxcpservice = yqueConfigService
                    .findWxcpservice();

            if(null != wxcpservice){
                IYqueTagGroup oldTagGroup = this.getById(tagGroup.getId());

                if (null != oldTagGroup) {

                    //标签名不同则更新企业微信端
                    if (!oldTagGroup.getGroupName().equals(tagGroup.getGroupName())) {
                        oldTagGroup.setGroupName(tagGroup.getGroupName());
                        //更新标签组名称
                        if (this.updateById(oldTagGroup)) {
                            //标签组属于客户标签, 则更新企业微信端标签组名称
                            if (tagGroup.getGroupTagType().equals(new Integer(1))) {
                                WxCpBaseResp wxCpBaseResp = wxcpservice.getExternalContactService()
                                        .editCorpTag(tagGroup.getGroupId(), tagGroup.getGroupName(), null);
                                if(!wxCpBaseResp.success()){
                                    throw new IYqueException("标签组名称编辑失败:"+wxCpBaseResp.success());
                                }
                            }
                        }

                    }


                    List<IYqueTag> tagList = tagGroup.getWeTags();
                    if(CollectionUtil.isNotEmpty(tagList)){

                        //处理标签同名的名表(比如web端当前标签组下，A标签删除了，然后又添加了A标签。)
                        List<IYqueTag> newWeTags = handleSameTagName(tagGroup, tagList);

                        List<IYqueTag> yTagIds = newWeTags.stream().filter(item->StringUtils.isNotEmpty(item.getTagId())).
                                collect(Collectors.toList());
                        log.info("处理的标签:"+ JSONUtil.toJsonStr(yTagIds));

                        if(CollectionUtil.isNotEmpty(yTagIds)){
                            //移除需要删除的标签
                            List<IYqueTag> removeIYqueTags = yqueTagService.list(new LambdaQueryWrapper<IYqueTag>()
                                    .eq(IYqueTag::getGroupId, tagGroup.getGroupId())
                                    .notIn(IYqueTag::getTagId, yTagIds.stream().map(IYqueTag::getTagId).collect(Collectors.toList())));
                            if(CollectionUtil.isNotEmpty(removeIYqueTags)){
                               if(yqueTagService.removeByIds(removeIYqueTags.stream().map(IYqueTag::getId).collect(Collectors.toList()))){
                                   WxCpBaseResp wxCpBaseResp = wxcpservice.getExternalContactService().delCorpTag(removeIYqueTags.stream()
                                           .map(IYqueTag::getTagId)
                                           .toArray(String[]::new), null);
                                   if(!wxCpBaseResp.success()){
                                       throw new IYqueException("编辑标签时,处理删除的标签失败:"+wxCpBaseResp.getErrmsg());
                                   }
                               }

                            }

                        }


                        List<IYqueTag> addNewTags = newWeTags.stream()
                                .filter(item -> StringUtils.isEmpty(item.getTagId()))
                                .collect(Collectors.toList());


                        //新增的标签
                        if(CollectionUtil.isNotEmpty(addNewTags)){
                            WxCpUserExternalTagGroupInfo tagGroupInfo=new WxCpUserExternalTagGroupInfo();
                            WxCpUserExternalTagGroupInfo.TagGroup tagGroupOne=new WxCpUserExternalTagGroupInfo.TagGroup();


                            List< WxCpUserExternalTagGroupInfo.Tag> tags=new ArrayList<>();

                            addNewTags.stream().forEach(item->{
                                WxCpUserExternalTagGroupInfo.Tag tagOne=new WxCpUserExternalTagGroupInfo.Tag();
                                tagOne.setName(item.getName());
                                tags.add(tagOne);
                            });

                            tagGroupOne.setTag(tags);
                            tagGroupOne.setGroupId(tagGroup.getGroupId());
                            tagGroupInfo.setTagGroup(tagGroupOne);

                            WxCpUserExternalTagGroupInfo wxCpUserExternalTagGroupInfo = wxcpservice.getExternalContactService().addCorpTag(tagGroupInfo);
                            if(!wxCpUserExternalTagGroupInfo.success()){
                                throw new IYqueException("编辑标签处理标签新增失败:"+wxCpUserExternalTagGroupInfo.getErrmsg());
                            }else{
                                List<IYqueTag> addNewTagsDb=new ArrayList<>();
                                wxCpUserExternalTagGroupInfo.getTagGroup().getTag().stream().forEach(k->{
                                    addNewTagsDb.add(IYqueTag.builder()
                                                    .delFlag(IYqueConstant.commonState)
                                                    .tagId(k.getId())
                                                    .name(k.getName())
                                                    .orderNumber(k.getOrder())
                                                    .groupId(tagGroup.getGroupId())
                                                    .tagType(1)
                                            .build());
                                });
                                yqueTagService.saveOrUpdateBatch(addNewTagsDb);

                            }


                        }


                    }


                }

            }



        } catch (Exception e) {

            log.error(e.getMessage());
            throw new IYqueException("编辑标签时,处理删除的标签失败:"+e.getMessage());

        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerGroupTagGroup(IYqueTagGroup tagGroup) {


        try {


                IYqueTagGroup oldTagGroup = this.getById(tagGroup.getId());

                if (null != oldTagGroup) {

                    //标签名不同则更新企业微信端
                    if (!oldTagGroup.getGroupName().equals(tagGroup.getGroupName())) {
                        oldTagGroup.setGroupName(tagGroup.getGroupName());
                        //更新标签组名称
                        this.updateById(oldTagGroup);

                    }

                    if (this.updateById(oldTagGroup)) {
                        List<IYqueTag> tagList = tagGroup.getWeTags();

                        if (CollectionUtil.isNotEmpty(tagList)) {
                            //处理标签同名的名表(比如web端当前标签组下，A标签删除了，然后又添加了A标签。)
                            List<IYqueTag> newWeTags = handleSameTagName(tagGroup, tagList);

                            List<IYqueTag> yTagIds = newWeTags.stream().filter(item -> StringUtils.isNotEmpty(item.getTagId())).
                                    collect(Collectors.toList());
                            log.info("处理的标签:" + JSONUtil.toJsonStr(yTagIds));

                            if (CollectionUtil.isNotEmpty(yTagIds)) {
                                //移除需要删除的标签
                                List<IYqueTag> removeIYqueTags = yqueTagService.list(new LambdaQueryWrapper<IYqueTag>()
                                        .eq(IYqueTag::getGroupId, tagGroup.getGroupId())
                                        .notIn(IYqueTag::getTagId, yTagIds.stream().map(IYqueTag::getTagId).collect(Collectors.toList())));
                                if (CollectionUtil.isNotEmpty(removeIYqueTags)) {
                                    yqueTagService.removeByIds(removeIYqueTags.stream().map(IYqueTag::getId).collect(Collectors.toList()));
                                }

                            }

                            List<IYqueTag> addNewTags = newWeTags.stream()
                                    .filter(item -> StringUtils.isEmpty(item.getTagId()))
                                    .collect(Collectors.toList());

                            //新增的标签
                            if (CollectionUtil.isNotEmpty(addNewTags)) {
                                addNewTags.stream().forEach(item -> {

                                    item.setTagType(2);
                                    item.setDelFlag(IYqueConstant.commonState);
                                    item.setOrderNumber(0L);
                                    item.setGroupId(tagGroup.getGroupId());

                                    if (StringUtils.isEmpty(item.getTagId())) {
                                        item.setTagId(SnowFlakeUtils.nextId().toString());
                                    }


                                });

                                yqueTagService.saveOrUpdateBatch(addNewTags);

                            }


                        }


                    }

                }


        } catch (Exception e) {

            log.error(e.getMessage());
            throw new IYqueException("编辑标签时,处理删除的标签失败:"+e.getMessage());

        }

    }

    //处理标签同名的名表(比如web端当前标签组下，A标签删除了，然后又添加了A标签。)
    private List<IYqueTag> handleSameTagName(IYqueTagGroup tagGroup,List<IYqueTag> iYqueTags){

        if(CollectionUtil.isNotEmpty(iYqueTags)){

            List<IYqueTag> addWeTags = iYqueTags.stream()
                    .filter(item -> StringUtils.isEmpty(item.getTagId()))
                    .collect(Collectors.toList());

            if(CollectionUtil.isNotEmpty(addWeTags)){
                List<IYqueTag> weTagList = yqueTagService.list(new LambdaQueryWrapper<IYqueTag>()
                        .in(IYqueTag::getName, addWeTags.stream().map(IYqueTag::getName).collect(Collectors.toList()))
                        .eq(IYqueTag::getGroupId, tagGroup.getGroupId()));
                if(CollectionUtil.isNotEmpty(weTagList)){
                    addWeTags.stream().forEach(item->{

                        Optional<IYqueTag> optional = weTagList.stream().filter(k -> k.getName().equals(item.getName())).findAny();
                        if(optional.isPresent()){
                            item.setId(
                                    optional.get().getId()
                            );
                            item.setTagId(
                                    optional.get().getTagId()
                            );
                        }
                    });
                }
            }

        }

        return iYqueTags;

    }

    @Override
     public List<IYqueTagGroup> findIYqueTagGroups(IYqueTagGroup yqueTagGroup) {
        List<IYqueTagGroup> iYqueTagGroupPage = this.baseMapper.selectList( new LambdaQueryWrapper<IYqueTagGroup>()
                        .eq(yqueTagGroup.getGroupTagType() !=null,IYqueTagGroup::getGroupTagType,yqueTagGroup.getGroupTagType())
                .like(StringUtils.isNotEmpty(yqueTagGroup.getGroupName()),IYqueTagGroup::getGroupName, yqueTagGroup.getGroupName())
                .orderByAsc(IYqueTagGroup::getOrderNumber));
        if(CollectionUtil.isNotEmpty(iYqueTagGroupPage)){

            List<IYqueTag> iYqueTags = yqueTagService.list(new LambdaQueryWrapper<IYqueTag>()
                    .in(IYqueTag::getGroupId, iYqueTagGroupPage.stream()
                            .map(IYqueTagGroup::getGroupId).collect(Collectors.toList())));
            if(CollectionUtil.isNotEmpty(iYqueTags)){
                iYqueTagGroupPage.stream().forEach(kk->{
                    kk.setWeTags(
                            iYqueTags.stream().filter(item->item.getGroupId().equals(kk.getGroupId())).sorted(Comparator.comparing(IYqueTag::getOrderNumber)).collect(Collectors.toList())
                    );
                });
            }
        }
        return iYqueTagGroupPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeGroupTags(String[] groupIds) {

        try {

            List<IYqueTagGroup> iYqueTagGroups = this.list(new LambdaQueryWrapper<IYqueTagGroup>().in(IYqueTagGroup::getGroupId, ListUtil.toList(groupIds)));

            if(CollectionUtil.isNotEmpty(iYqueTagGroups)){
                //删除标签组
                if(this.remove(new LambdaQueryWrapper<IYqueTagGroup>()
                        .in(IYqueTagGroup::getGroupId, ListUtil.toList(groupIds)))
                        &&yqueTagService.remove(new LambdaQueryWrapper<IYqueTag>()
                        .in(IYqueTag::getGroupId,ListUtil.toList(groupIds)))){

                    iYqueTagGroups.stream().forEach(item->{

                        if(item.getGroupTagType().equals(new Integer(1))){//客户标签则同步企业微信做删除

                            WxCpBaseResp wxCpBaseResp = null;
                            try {
                                wxCpBaseResp = yqueConfigService
                                        .findWxcpservice()
                                        .getExternalContactService().delCorpTag(null, groupIds);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                            if(!wxCpBaseResp.success()){
                                throw new IYqueException("标签组删除失败:"+wxCpBaseResp.getErrmsg());
                            }
                        }

                    });

                }
            }


        }catch (Exception e){
            log.error("标签组删除失败:"+e.getMessage());
            throw new IYqueException(e.getMessage());
        }


    }


}
