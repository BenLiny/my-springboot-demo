package com.eastcom.my_springboot_demo.kafka.faststart.producer;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

/**
 * ClassName: ProductFastStart <br/>
 * Function:  生产者客户端实例<br/>
 * Reason:  生产者客户端实例<br/>
 * date: 2019/12/2 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
public class ProductFastStart {

  private static final String topicName = "local_topic_demon";

  private static final String SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";

  private static final String brokerList = "192.168.233.128:9091";

  public static void main(String[] args){
    //配置参数
    Properties properties = new Properties();
    properties.put("key.serializer",SERIALIZER);
    properties.put("value.serializer",SERIALIZER);
    properties.put("bootstrap.servers",brokerList);
    //创建kafka producer实例
    KafkaProducer<String,String> producer = new KafkaProducer<>(properties);

    //测试，发送消息
    ProducerRecord<String,String> record = new ProducerRecord<>(topicName,"hello,Kafka");

    //发送
    try {
      producer.send(record);
    }catch (Exception e){
      e.printStackTrace();
    }finally {
      producer.close();//关闭连接
    }
  }
}
