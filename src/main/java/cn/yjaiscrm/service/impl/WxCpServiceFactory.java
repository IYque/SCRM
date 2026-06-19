package cn.iyque.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.iyque.entity.IYqueConfig;
import me.chanjar.weixin.cp.api.WxCpService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;

import java.util.Optional;

public class WxCpServiceFactory {

    public static WxCpService createWxCpService(IYqueConfig iYqueConfig) {
        return Optional.ofNullable(iYqueConfig)
                .filter(config -> StrUtil.isNotEmpty(config.getCorpId())
                        && StrUtil.isNotEmpty(config.getAgentId())
                        && StrUtil.isNotEmpty(config.getAgentSecert())
                        && StrUtil.isNotEmpty(config.getToken())
                        && StrUtil.isNotEmpty(config.getEncodingAESKey()))
                .map(config -> {
                    WxCpDefaultConfigImpl wxCpConfig = new WxCpDefaultConfigImpl();
                    wxCpConfig.setCorpId(config.getCorpId());
                    wxCpConfig.setCorpSecret(config.getAgentSecert());
                    wxCpConfig.setAgentId(Integer.parseInt(config.getAgentId()));
                    wxCpConfig.setToken(config.getToken());
                    wxCpConfig.setAesKey(config.getEncodingAESKey());
                    wxCpConfig.setMsgAuditLibPath(config.getMsgAuditLibPath());
                    wxCpConfig.setMsgAuditPriKey(config.getMsgAuditPriKey());
                    wxCpConfig.setMsgAuditSecret(config.getMsgAuditSecret());
                    WxCpServiceImpl wxCpService = new WxCpServiceImpl();
                    wxCpService.setWxCpConfigStorage(wxCpConfig);
                    return wxCpService;
                })
                .orElse(null);
    }
}
