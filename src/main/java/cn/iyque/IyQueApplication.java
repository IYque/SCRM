package cn.iyque;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class IyQueApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(IyQueApplication.class)
                .build().run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ iyque-code启动成功   ლ(´ڡ`ლ)ﾞ ");
    }
}
