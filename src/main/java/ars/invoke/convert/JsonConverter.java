package ars.invoke.convert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ars.util.Jsons;

/**
 * 基于json格式的对象数据转换实现
 *
 * @author wuyongqiang
 */
public class JsonConverter implements Converter {
    protected final int depth; // json转换对象属性下钻深度
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    public JsonConverter() {
        this(2);
    }

    public JsonConverter(int depth) {
        this.depth = depth;
    }

    @Override
    public String serialize(Object object) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Before serialize: {}", object);
        }
        String json = Jsons.format(object, this.depth);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("After serialize: {}", json);
        }
        return json;
    }

    @Override
    public Object deserialize(String string) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Before deserialize: {}", string);
        }
        Object object = Jsons.parse(string);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("After deserialize: {}", object);
        }
        return object;
    }

}
