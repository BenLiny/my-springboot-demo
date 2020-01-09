package com.eastcom.my_springboot_demo.zookeeper.watcher;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ClassName: WatcherApi <br/>
 * Function:  实现watcher监听<br/>
 * Reason:  实现watcher监听<br/>
 * date: 2019/10/30 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
public class WatcherApi implements Watcher {

  private static final Logger logger = LoggerFactory.getLogger(WatcherApi.class);

  @Override
  public void process(WatchedEvent event) {
    logger.info("【Watcher监听事件】={}",event.getState());
    logger.info("【监听路径为】={}",event.getPath());
    logger.info("【监听的类型为】={}",event.getType()); //  三种监听类型： 创建，删除，更新
  }

}
