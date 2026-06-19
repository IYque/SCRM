package cn.iyque.utils;

public class PromptUtils {
    public static String escape(String input) {
        // 防止 {xxx} 被 LangChain4j 当作变量解析（如果你的 prompt 模板用了 {{}} 则可省略）
        return input.replace("{", "\\{").replace("}", "\\}");
    }
}
