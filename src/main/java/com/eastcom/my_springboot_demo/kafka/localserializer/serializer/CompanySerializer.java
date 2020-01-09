package com.eastcom.my_springboot_demo.kafka.localserializer.serializer;

import java.nio.ByteBuffer;
import java.util.Map;

import com.eastcom.my_springboot_demo.kafka.vo.Company;

import org.apache.kafka.common.serialization.Serializer;

/**
 * ClassName: CompanySerializer <br/>
 * Function:  自定义序列化工具<br/>
 * Reason:  生产者需要把数据序列化为字节数组    消费者需要把数据从字节数组反序列化  要对应<br/>
 * date: 2019/12/2 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
public class CompanySerializer implements Serializer<Company> {
  @Override
  public void configure(Map<String, ?> map, boolean b) {

  }

  @Override
  public byte[] serialize(String topic, Company data) {
    if (data == null){
      return null;
    }

    byte[] name,address;

    try {

      if (data.getName() != null){
        name = data.getName().getBytes("UTF-8");
      }else {
        name = new byte[0];
      }

      if (data.getAddress() != null){
        address = data.getAddress().getBytes("UTF-8");
      }else {
        address = new byte[0];
      }

      ByteBuffer buffer = ByteBuffer.allocate(4+4+name.length + address.length);
      buffer.putInt(name.length);
      buffer.put(name);
      buffer.putInt(address.length);
      buffer.put(address);
      return buffer.array();

    }catch (Exception e){
      e.printStackTrace();
    }
    return new byte[0];
  }

  @Override
  public void close() {

  }
}
