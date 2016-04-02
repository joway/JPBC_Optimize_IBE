package encrypt.model;

import java.io.Externalizable;

/**
 * @Author joway
 * @Email joway.w@gmail.com
 * @Date 16/3/31.
 */
public interface CommonText extends Externalizable {
    /*
    由于jpbc库的Element类型不支持序列化, 所以使用这个java原生的自定义序列化接口,
    来达到序列化的目的.
     */
    String toString();
}
