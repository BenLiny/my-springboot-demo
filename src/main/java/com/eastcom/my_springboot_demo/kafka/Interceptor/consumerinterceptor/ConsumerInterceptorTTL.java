package com.eastcom.my_springboot_demo.kafka.Interceptor.consumerinterceptor;

import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

/**
 * ClassName: ConsumerInterceptorTTL <br/>
 * Function:  自定义消费者拦截器<br/>
 * Reason:  <br/>
 * date: 2019/12/4 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
public class ConsumerInterceptorTTL implements ConsumerInterceptor<String,String> {

  //poll之前调用
  @Override
  public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> consumerRecords) {
    return null;
  }

  //提交完消费位移后调用
  @Override
  public void onCommit(Map<TopicPartition, OffsetAndMetadata> map) {

  }

  @Override
  public void close() {

  }

  @Override
  public void configure(Map<String, ?> map) {

  }
}
