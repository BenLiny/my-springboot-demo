package com.eastcom.my_springboot_demo.kafka.faststart.consumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Component;

/**
 * ClassName: ConsumerFastStart <br/>
 * Function: 消费者实例 <br/>
 * Reason: 消费者实例 <br/>
 * date: 2019/12/2 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
@Component
public class ConsumerFastStart {

  private static final String brokerList = "192.168.233.128:9091";

  private static final String topicName = "local_topic_demon";

  private static final String groupId = "group.demo";//设置消费组名称

  private static final String deserializer = "org.apache.kafka.common.serialization.StringDeserializer";

  public static void main(String[] args){
    Properties properties = new Properties();
    properties.put("key.deserializer",deserializer);
    properties.put("value.deserializer",deserializer);
    properties.put("bootstrap.servers",brokerList);

    properties.put("group.id",groupId);

    //创建消费者实例
    KafkaConsumer<String,String> consumer = new KafkaConsumer<>(properties);

    //订阅主题
    consumer.subscribe(Collections.singletonList(topicName));

    while (true){
      ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(1000));

      for (ConsumerRecord<String,String> record : records){
        System.out.println(record.value());
      }
    }

  }
}
