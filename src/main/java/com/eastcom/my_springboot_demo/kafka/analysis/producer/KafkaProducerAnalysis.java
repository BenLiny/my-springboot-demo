package com.eastcom.my_springboot_demo.kafka.analysis.producer;

import java.util.Properties;

import com.eastcom.my_springboot_demo.kafka.Interceptor.producerinterceptor.LocalProducerInterceptor;
import com.eastcom.my_springboot_demo.kafka.Interceptor.producerinterceptor.ProducerInterceptorPrefix;
import com.eastcom.my_springboot_demo.kafka.localserializer.serializer.CompanySerializer;
import com.eastcom.my_springboot_demo.kafka.partitioner.DemoPartitioner;
import com.eastcom.my_springboot_demo.kafka.vo.Company;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * ClassName: KafkaProducerAnalysis <br/>
 * Function:  <br/>
 * Reason:  <br/>
 * date: 2019/12/2 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
public class KafkaProducerAnalysis {

  private static final String brokerList = "192.168.233.128:9091";

  private static final String topic = "local_topic_demon";

  //private static final String SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";

  private static Properties initConfig(){
    Properties props = new Properties();

//    props.put("bootstrap.servers",brokerList);
//    props.put("key.serializer",SERIALIZER);
//    props.put("value.serializer",SERIALIZER);

    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,brokerList);

    //字符串序列化器
//    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,SERIALIZER);
//    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,SERIALIZER);

//    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

    //自定义序列化器
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CompanySerializer.class.getName());

    /*
     * 设置对应的客户端id 默认为""
     */
    //props.put("client.id","producer.client.id.demo");
    props.put(ProducerConfig.CLIENT_ID_CONFIG,"producer.client.id.demo");

    //调用自定义的分区器
    props.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, DemoPartitioner.class.getName());

    //调用自定义的拦截器
    //props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ProducerInterceptorPrefix.class.getName());

    //也可以配多个拦截器，用，隔开
    props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,LocalProducerInterceptor.class.getName() + "," + ProducerInterceptorPrefix .class.getName());

    return props;
  }

  public static void main(String[] args){
    Properties properties = initConfig();

    KafkaProducer<String, Company> producer = new KafkaProducer<>(properties);

    //KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties,new StringSerializer(),new StringSerializer());

    int i = 0;
    while (i < 5){
      //ProducerRecord<String,String> record = new ProducerRecord<>(topic,"Hello kafka" + i++);
      ProducerRecord<String, Company> record = new ProducerRecord<>(topic,Company.builder().name("lyh").address("东方通信").build());

      try {
        //producer.send(record);异步

      /*
       同步
       */
//      Future<RecordMetadata> future = producer.send(record);
//      RecordMetadata recordMetadata = future.get();
      /*
       topic 主题   partition 分区  offset 备份
       */
//      System.out.println(recordMetadata.topic() + "-" + recordMetadata.partition() + "-" + recordMetadata.offset());

      /*回调函数实现更可靠的异步
             callback可以保证分区有序
       */
        producer.send(record, (recordMetadata, e) -> {
          if (e != null){
            //不太好的写法
            e.printStackTrace();
          }else {
            System.out.println(recordMetadata.topic() + "-" + recordMetadata.partition() + "-" + recordMetadata.offset());
          }
        });

        i++;

      }catch (Exception e){
        e.printStackTrace();
      }
    }
    producer.close();
  }
}
