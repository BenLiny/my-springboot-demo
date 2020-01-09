package com.eastcom.my_springboot_demo.kafka.Interceptor.producerinterceptor;

import java.util.Map;

import com.eastcom.my_springboot_demo.kafka.vo.Company;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

/**
 * ClassName: ProducerInterceptorPrefix <br/>
 * Function:  生产者拦截器<br/>
 * Reason:  生产者拦截器<br/>
 * date: 2019/12/3 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
public class ProducerInterceptorPrefix implements ProducerInterceptor<String, Company> {

  private volatile long sendSuccess = 0;

  private volatile long sendFailure = 0;

  /*
   * 在消息序列化和计算分区之前执行onSend
   */
  @Override
  public ProducerRecord<String, Company> onSend(ProducerRecord<String, Company> producerRecord) {

    producerRecord.value().setName(producerRecord.value().getName() + "发送消息给：");
    return new ProducerRecord<>(producerRecord.topic(),producerRecord.partition(),
            producerRecord.timestamp(),producerRecord.key(),producerRecord.value(),producerRecord.headers());
    //return null;
  }

  /*
   * 消息被应答之前或者发送失败时调用    先于Callback执行
   */
  @Override
  public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {

    if (e == null){
      sendSuccess++;
    }else {
      sendFailure++;
    }
  }

  /*
   * 关闭拦截器的时候执行
   */
  @Override
  public void close() {
    double successRatio = (double) sendSuccess / (sendFailure + sendSuccess);

    System.out.println("[INFO] 发送成功率=" + String.format("%f",successRatio * 100) + "%");
  }

  //父类方法
  @Override
  public void configure(Map<String, ?> map) {

  }
}
