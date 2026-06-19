package cn.iyque.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "iyque")
@Data
public class IYqueParamConfig {

    private String userName;
    private String pwd;

    private Boolean demo=false;

    private String uploadDir;

    //文件预览访问前缀
    private String fileViewUrl;


    //是否预审所有数据,fasle默认预审当天0点到此刻的数据，true所有数据
    private Boolean inquiryAll=false;

    //投诉页面
    private String complaintUrl="https://iyque.cn";


    //客户公海页面
    private String customerSeasUrl="https://iyque.cn";

    //h5营销链接地址
    private String h5MarketUrl="https://iyque.cn";


    //向量数据库相关配置
    private VectorStoreParam vector;



    //三方登录相关参数
    private ThreeLoginParam threeLoginParam=new ThreeLoginParam();





    @Data
    public static class ThreeLoginParam{

        //是否启动三方登录，默认是不启动
        private boolean startThreeLogin=false;


        //gitee登录相关信息
        private GiteeLoginParam giteeLoginParam=new GiteeLoginParam();





    }


    @Data
    public static class GiteeLoginParam{


        //获取授权码code地址
        private String threeLoginUrl="https://gitee.com/oauth/authorize?client_id={0}&redirect_uri={1}&response_type=code";


        //获取token地址
        private String getTokenUrl ="https://gitee.com/oauth/token";


        //判断用户是否star的url
        private String starUlr="https://gitee.com/api/v5/user/starred/iyque/iYqueCode?access_token={0}";


        //star仓库的url
        private String starRepositoryUrl="https://gitee.com/api/v5/user/starred/iyque/iYqueCode";



        //客户端id
        private String clientId;

        //客户端密钥
        private String clientSecret;

        //重定向地址
        private String redirectUri;

    }



    //向量相关设置
    @Data
    public static class VectorStoreParam{

        //片段截取字符数,控制分片的长度，确保均匀分割
        private int chunkSize=1000;
        //重叠数	保留分片边界的上下文，避免语义断裂,通常设置为片段数的10%-20%之间
        private int chunkOverlap=200;

        //向量维度（需与实际数据匹配）
        private  Integer dimension=1536;

        //自定义集合名称
        private  String collectionName;

        //向量模型
        private String vectorModel;

        //余弦相似度
        private Float score=0.5f;

        //Milvus地址
        private String host;
        //Milvus端口
        private String port;
        //模型的apikey
        private String apiKey;
        //模型的请求地址
        private String apiUrl;

    }
}
