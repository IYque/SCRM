package cn.iyque.chain.split;


import cn.iyque.config.IYqueParamConfig;
import io.github.lnyocly.ai4j.utils.RecursiveCharacterTextSplitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@Slf4j
@Primary
public class CharacterTextSplitter implements TextSplitter {

    @Autowired
    private IYqueParamConfig iYqueParamConfig;



    @Override
    public List<String> split(String content) {

        RecursiveCharacterTextSplitter recursiveCharacterTextSplitter = new RecursiveCharacterTextSplitter(iYqueParamConfig.getVector().getChunkSize(),
                iYqueParamConfig.getVector().getChunkOverlap());
        return recursiveCharacterTextSplitter.splitText(content);

    }
}

