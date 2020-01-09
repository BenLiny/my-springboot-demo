package com.eastcom.my_springboot_demo.kafka.analysis.consumer;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import com.eastcom.my_springboot_demo.kafka.localserializer.deserializer.CompanyDeserializer;
import com.eastcom.my_springboot_demo.zookeeper.config.ZookeeperConfig;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName: KafkaConsumerAnalysis <br/>
 * Function:  <br/>
 * Reason:  <br/>
 * date: 2019/12/3 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
public class KafkaConsumerAnalysis {

  private static final Logger LOG = LoggerFactory.getLogger(ZookeeperConfig.class);

  private static final String brokerList = "192.168.233.128:9091";

  private static final String topicName = "local_topic_demon";

  private static final String groupId = "group.demo";

  private static final AtomicBoolean isRunning = new AtomicBoolean(true);

  private static Properties initConfig(){
    Properties properties = new Properties();

    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

    //properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());

    //启用自定义的反序列化器，返回实体类
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CompanyDeserializer.class.getName());

    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,brokerList);

    properties.put(ConsumerConfig.CLIENT_ID_CONFIG,"consumer.client.id.demo");

    properties.put(ConsumerConfig.GROUP_ID_CONFIG,groupId);

    //关闭kafka自动提交
    properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,false);

    return properties;
  }

  public static void main(String[] args){

    Properties props = initConfig();

    KafkaConsumer<String,String> consumer = new KafkaConsumer<>(props);

    //一个消费者可以订阅多个主题
    //consumer.subscribe(Collections.singletonList(topicName));

    /* 多个主题可以写表达式
       多次调用subscribe取最后一次
     */
    //consumer.subscribe(Pattern.compile("local_.*"));

    //订阅某个分区
    //consumer.assign(Collections.singletonList(new TopicPartition(topicName, 0)));

    //通过PartitionsFor获取主题信息
    /*List<TopicPartition> topicPartitions = new ArrayList<>();
    List<PartitionInfo> partitionInfos = consumer.partitionsFor(topicName);
    if (partitionInfos != null){
      for (PartitionInfo partitionInfo : partitionInfos){
        topicPartitions.add(new TopicPartition(partitionInfo.topic(),partitionInfo.partition()));
      }
    }

    consumer.assign(topicPartitions);*/

    //long lastConsumedOffset = -1; //当前的偏移量

    Map<TopicPartition,OffsetAndMetadata> currentOffset = new HashMap<>();

    /*
     * 订阅主题时设定再均衡监听器
     */
    consumer.subscribe(Collections.singletonList(topicName), new ConsumerRebalanceListener() {

      /*
       * 再均衡开始之前，消费者停止消费之后进行
       * 在此进行消费位移提交
       */
      @Override
      public void onPartitionsRevoked(Collection<TopicPartition> collection) {
        consumer.commitSync(currentOffset);
        currentOffset.clear();
      }

      @Override
      public void onPartitionsAssigned(Collection<TopicPartition> collection) {

        /*for (TopicPartition tp : collection){
          consumer.seek(tp,getOffsetFromDb(tp));
        }*/
      }
    });

    try {
      while (isRunning.get()){
        ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(1000));

        for (TopicPartition topicPartition : records.partitions()){//获取消息集里的所有分区
          List<ConsumerRecord<String,String>> consumerRecords = records.records(topicPartition);
          for (ConsumerRecord record : consumerRecords){
            currentOffset.put(new TopicPartition(record.topic(),record.partition()),new OffsetAndMetadata(record.offset() + 1));
            System.out.println("分区为： " + record.partition() + "， 消息为： " + record.value());
          }
        }
        consumer.commitAsync(currentOffset,null);// 同步提交偏移量


        /*for (ConsumerRecord record : records){
          System.out.println("topic = " + record.topic() + ", partition = " + record.partition() + ", offset = " + record.offset());

          System.out.println("key = " + record.key() + ", value = " + record.value());
        }*/
      }
    }catch (Exception e){
      LOG.error("occur exception " , e);
    }finally {
      consumer.close();
    }
  }
}
