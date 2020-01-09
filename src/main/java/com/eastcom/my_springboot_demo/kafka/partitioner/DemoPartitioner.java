package com.eastcom.my_springboot_demo.kafka.partitioner;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

/**
 * ClassName: DemoPartitioner <br/>
 * Function:  自定义分区器<br/>
 * Reason:  分区器指定消息发向kafka哪个分区    默认为DefaultPartitioner<br/>
 * date: 2019/12/3 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
public class DemoPartitioner implements Partitioner {

  private final AtomicInteger counter = new AtomicInteger(0);

  @Override
  public int partition(String topic, Object o, byte[] keyBytes, Object o1, byte[] valueBytes, Cluster cluster) {
    List<PartitionInfo> partitioners = cluster.partitionsForTopic(topic);
    int numPartitions = partitioners.size();
    if (null == keyBytes){
      return counter.getAndIncrement() % numPartitions;
    }else {
      return Utils.toPositive(Utils.murmur2(keyBytes)) % numPartitions;
    }
    //return 0;
  }

  @Override
  public void close() {

  }

  //Partition父类的方法
  @Override
  public void configure(Map<String, ?> map) {

  }
}
