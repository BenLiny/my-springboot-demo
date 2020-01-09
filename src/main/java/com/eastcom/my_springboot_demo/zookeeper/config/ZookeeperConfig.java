package com.eastcom.my_springboot_demo.zookeeper.config;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.annotation.PostConstruct;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * ClassName: ZookeeperConfig <br/>
 * Function:  zookeeper配置类<br/>
 * Reason:  zookeeper配置类<br/>
 * date: 2019/10/30 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
@Component
public class ZookeeperConfig {

  //zookeeper连接的ip和端口
  @Value("${zookeeper.address}")
  private String ipAndPort;

  //zookeeper连接超时时间
  @Value("${zookeeper.timeout}")
  private int timeout;

  private static ZooKeeper zkClient = null;

  //记录日志
  private static final Logger LOG = LoggerFactory.getLogger(ZookeeperConfig.class);

  @PostConstruct
  public void getZookeeper(){

    try{
       final CountDownLatch countDownLatch = new CountDownLatch(1);

      //连接成功后，会回调watcher监听，此连接操作是异步的，执行完new语句后，直接调用后续代码
      //  可指定多台服务地址 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
      zkClient = new ZooKeeper(ipAndPort, timeout, watchedEvent -> {
        if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
          countDownLatch.countDown();
        }
      });
      countDownLatch.await();
      LOG.info("【初始化ZooKeeper连接状态....】={}", zkClient.getState());

    }catch (Exception e){
      LOG.error("初始化ZooKeeper连接异常....】={}",e);
    }

  }

  /**
   * 判断指定节点是否存在
   * @param path 路径
   * @param needWatch  指定是否复用zookeeper中默认的Watcher
   */
  public Stat exists(String path, boolean needWatch){
    try {
      return zkClient.exists(path,needWatch);
    }catch (Exception e){
      LOG.error("【判断指定节点是否存在异常】{},{}",path,e);
      return null;
    }
  }

  /**
   *  检测结点是否存在 并设置监听事件
   *      三种监听类型： 创建，删除，更新
   *
   * @param path 路径
   * @param watcher  传入指定的监听类
   */
  public Stat exists(String path, Watcher watcher ){
    try {
      return zkClient.exists(path,watcher);
    } catch (Exception e) {
      LOG.error("【断指定节点是否存在异常】{},{}",path,e);
      return null;
    }
  }

  /**
   * 创建持久化节点
   * @param path 路径
   * @param data 数据
   */
  public boolean createNode(String path, String data){
    try {
      zkClient.create(path,data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
      return true;
    } catch (Exception e) {
      LOG.error("【创建持久化节点异常】{},{},{}",path,data,e);
      return false;
    }
  }

  /**
   * 修改持久化节点
   * @param path 路径
   * @param data 数据
   */
  public boolean updateNode(String path, String data){
    try {
      //zk的数据版本是从0开始计数的。如果客户端传入的是-1，则表示zk服务器需要基于最新的数据进行更新。如果对zk的数据节点的更新操作没有原子性要求则可以使用-1.
      //version参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败. 指定version为-1则忽略版本检查
      zkClient.setData(path,data.getBytes(),-1);
      return true;
    } catch (Exception e) {
      LOG.error("【修改持久化节点异常】{},{},{}",path,data,e);
      return false;
    }
  }

  /**
   * 删除持久化节点
   * @param path 路径
   */
  public boolean deleteNode(String path){
    try {
      //version参数指定要更新的数据的版本, 如果version和真实的版本不同, 更新操作将失败. 指定version为-1则忽略版本检查
      zkClient.delete(path,-1);
      return true;
    } catch (Exception e) {
      LOG.error("【删除持久化节点异常】{},{}",path,e);
      return false;
    }
  }

  /**
   * 获取当前节点的子节点(不包含孙子节点)
   * @param path 父节点path
   */
  public List<String> getChildren(String path) throws KeeperException, InterruptedException{
    return zkClient.getChildren(path, false);
  }

  /**
   * 获取指定节点的值
   * @param path 路径
   */
  public  String getData(String path,Watcher watcher){
    try {
      Stat stat=new Stat();
      byte[] bytes=zkClient.getData(path,watcher,stat);
      return  new String(bytes);
    }catch (Exception e){
      e.printStackTrace();
      return  null;
    }
  }

}
