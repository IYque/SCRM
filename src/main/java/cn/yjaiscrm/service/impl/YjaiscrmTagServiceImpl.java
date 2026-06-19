package cn.iyque.service.impl;

import cn.iyque.entity.IYqueTag;
import cn.iyque.mapper.IYqueTagMapper;
import cn.iyque.service.IYqueConfigService;
import cn.iyque.service.IYqueTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.util.json.GsonParser;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.bean.external.WxCpUserExternalTagGroupInfo;
import me.chanjar.weixin.cp.util.json.WxCpGsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class IYqueTagServiceImpl extends ServiceImpl<IYqueTagMapper, IYqueTag> implements IYqueTagService {


    @Autowired
    private IYqueConfigService iYqueConfigService;



    @Override
    public List<WxCpUserExternalTagGroupInfo.Tag> listAll() throws Exception {

        WxCpService wxCpServic = iYqueConfigService.findWxcpservice();

        String url = wxCpServic.getWxCpConfigStorage().getApiUrl("/cgi-bin/externalcontact/get_corp_tag_list");
        String responseContent = wxCpServic.get(url, (String)null);
        JsonObject tmpJson = GsonParser.parse(responseContent);
        List<WxCpUserExternalTagGroupInfo.TagGroup> tagGroups = (List) WxCpGsonBuilder.create().fromJson(tmpJson.get("tag_group"), (new TypeToken<List<WxCpUserExternalTagGroupInfo.TagGroup>>() {
        }).getType());

        List<WxCpUserExternalTagGroupInfo.Tag> tags = new ArrayList<>();
        tagGroups.forEach(tagGroup -> tags.addAll(tagGroup.getTag()));

        return tags;
    }
}
