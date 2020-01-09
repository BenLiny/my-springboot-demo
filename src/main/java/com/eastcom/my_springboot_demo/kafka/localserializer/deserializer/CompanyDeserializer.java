package com.eastcom.my_springboot_demo.kafka.localserializer.deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;

import com.eastcom.my_springboot_demo.kafka.vo.Company;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;


/**
 * ClassName: CompanyDeserializer <br/>
 * Function:  自定义反序列化器<br/>
 * Reason:  消费的时候 把kafka消息反序列化为java对象<br/>
 * date: 2019/12/3 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
public class CompanyDeserializer implements Deserializer<Company> {

  @Override
  public void configure(Map<String, ?> map, boolean b) {

  }

  @Override
  public Company deserialize(String topic, byte[] data) {
    if (data == null){
      return null;
    }
    if (data.length < 8){
      throw new SerializationException("Size of data received by DemoDeserializer is shorter than exception!");
    }

    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    int nameLen , addressLen;
    String name,address;

    nameLen = byteBuffer.getInt();
    byte[] nameBytes = new byte[nameLen];
    byteBuffer.get(nameBytes);

    addressLen = byteBuffer.getInt();
    byte[] addressBytes = new byte[addressLen];
    byteBuffer.get(addressBytes);

    try {
      name = new String(nameBytes,"UTF-8");
      address = new String(addressBytes,"UTF-8");
    }catch (Exception e){
      throw new SerializationException("Error occur when deserializer!");
    }

    return new Company(name,address);
  }

  @Override
  public void close() {

  }
}
