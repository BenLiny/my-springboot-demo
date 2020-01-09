package com.eastcom.my_springboot_demo.kafka.Interceptor.producerinterceptor;

import java.util.Map;

import com.eastcom.my_springboot_demo.kafka.vo.Company;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * ClassName: LocalProducerInterceptor <br/>
 * Function:  local生产者拦截器<br/>
 * Reason:  用于测试<br/>
 * date: 2019/12/3 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
public class LocalProducerInterceptor implements ProducerInterceptor<String, Company> {

  @Override
  public ProducerRecord<String, Company> onSend(ProducerRecord<String, Company> producerRecord) {
    producerRecord.value().setName(producerRecord.value().getName() + " ");
    return new ProducerRecord<>(producerRecord.topic(),producerRecord.partition(),producerRecord.timestamp(),producerRecord.key(),
            producerRecord.value(),producerRecord.headers());
  }

  @Override
  public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {

  }

  @Override
  public void close() {

  }

  @Override
  public void configure(Map<String, ?> map) {

  }
}
