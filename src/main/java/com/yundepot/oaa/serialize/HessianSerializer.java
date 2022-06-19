package com.yundepot.oaa.serialize;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import com.yundepot.oaa.exception.DeserializationException;
import com.yundepot.oaa.exception.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * @author zhaiyanan
 * @date 2019/5/21 19:29
 */
public final class HessianSerializer implements Serializer{

    private SerializerFactory serializerFactory = new SerializerFactory();

    @Override
    public byte[] serialize(Object object, Map<String, String> context) throws SerializationException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(outputStream);
        output.setSerializerFactory(serializerFactory);
        try {
            output.writeObject(object);
            output.close();
        } catch (IOException e) {
            throw new SerializationException("IOException occurred when Hessian serializer encode", e);
        }
        return outputStream.toByteArray();
    }

    @Override
    public Object deserialize(byte[] data, Map<String, String> context) throws DeserializationException {
        Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(data));
        input.setSerializerFactory(serializerFactory);
        Object resultObject = null;
        try {
            resultObject = input.readObject();
            input.close();
        } catch (IOException e) {
            throw new DeserializationException("IOException occurred when Hessian serializer decode", e);
        }
        return resultObject;
    }
}
