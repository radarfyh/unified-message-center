package ltd.huntinginfo.feng.common.xss.core;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.exc.MismatchedInputException;

/**
 * Jackson XSS 处理基类 (适配 Jackson 3.x)
 *
 * @author lengleng
 * @date 2025/05/31
 */
public abstract class XssCleanDeserializerBase extends ValueDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctx) throws JacksonException {
        JsonToken jsonToken = p.currentToken();
        if (jsonToken != JsonToken.VALUE_STRING) {
            throw MismatchedInputException.from(p, String.class,
                    "mica-xss: can't deserialize value of type java.lang.String from " + jsonToken);
        }
        // 获取字符串值
        String text = p.getValueAsString();
        if (text == null) {
            return null;
        }
        // 使用 currentName() 获取当前字段名
        return this.clean(p.currentName(), text);
    }

    /**
     * 清理文本中的 XSS 攻击内容
     * @param name 字段名
     * @param text 待清理的文本内容
     * @return 清理后的安全文本
     * @throws JacksonException 清理过程中发生异常时抛出
     */
    public abstract String clean(String name, String text) throws JacksonException;
}