package com.eastcom.my_springboot_demo.websocket.handler;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eastcom.my_springboot_demo.zookeeper.config.ZookeeperConfig;
import com.eastcom.my_springboot_demo.zookeeper.watcher.WatcherApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * ClassName: TextMessageHandler <br/>
 * Function:  配置WebSocket建立连接时的处理<br/>
 * Reason:  配置WebSocket建立连接时的处理<br/>
 * date: 2019/10/28 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */

public class TextMessageHandler extends TextWebSocketHandler {

  //用于存放所有建立链接的对象
  private Map<String, WebSocketSession> allClients = new HashMap<>();

  //用于存放所有任务线程
  private Map<String, List<Timer>> allTasks = new HashMap<>();

  //记录日志
  private static final Logger LOG = LoggerFactory.getLogger(TextMessageHandler.class);

  private ZookeeperConfig zkc = new ZookeeperConfig();

  /**
   * 处理文本消息,模拟web给服务端推送消息
   * session   当前发送消息的用户的链接
   * message   发送的消息是什么
   * */
  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message){
    JSONObject jsonObject = JSON.parseObject(new String(message.asBytes()));
    String to = jsonObject.getString("toUser");  //找到接收者
    String toMessage = jsonObject.getString("toMessage"); //获取到发送的内容
    String fromUser = (String) session.getAttributes().get("name");  //获取到当前发送消息的用户姓名
    String content = "收到来自"+fromUser+"的消息，内容是:"+toMessage; //拼接的字符串
    LOG.info(content);
    TextMessage toTextMessage = new TextMessage(content);//创建消息对象
    sendMessage(to,toTextMessage);  //一个封装的方法，进行点对点的发送数据
  }

  //发送消息的封装方法
  private void sendMessage(String toUser, TextMessage message){
    //获取到对方的链接
    WebSocketSession session = allClients.get(toUser);//获取到对方的链接
    if (session != null && session.isOpen()) {
      try {
        session.sendMessage(message);//发送消息
        LOG.info("发送消息给" + toUser + ",内容是：" + message.getPayload());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 当链接建立的时候调用
   * */
  @Override
  public void afterConnectionEstablished(WebSocketSession session){
    LOG.info("WebSocket连接成功！");
    String name = (String) session.getAttributes().get("name");//获取到拦截器中设置的name
    if (name != null) {
      allClients.put(name,session);//保存当前用户和链接的关系

      /*
       * 模拟服务端定时给web推消息
       * 起个定时线程，2000毫秒执行一次,执行10000毫秒关闭线程
       */
      Timer timer = new Timer();
      Date time = Calendar.getInstance().getTime();
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          sendMessage(name,new TextMessage("当前时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        }
      }, time,2000);

      String path = "/" + name;//生成zookeeper的存放路径
      String data = timer.toString();//zookeeper中存的具体内容

      /*
       * 保存当前用户和对应任务线程的关系
       */
      if (allTasks.get(name) != null && allTasks.get(name).size() > 0){
        allTasks.get(name).add(timer);
        saveToZookeeper(path, data);
      }else {
        List<Timer> allTask = new ArrayList<>();
        allTask.add(timer);
        allTasks.put(name,allTask);
        saveToZookeeper(path, data);
      }

    }else {
      LOG.info("未传入name，无法确认session会话");
    }
  }

  /**
   * 存入zookeeper的相关操作
   * @param path 路径
   * @param data 数据
   */
  private void saveToZookeeper(String path, String data) {
    if (zkc.exists(path,true) != null){
      zkc.updateNode(path,data);
      LOG.info("修改zookeeper结点，结点为：{}，数据为：{}",path,zkc.getData(path,new WatcherApi()));
    }else {
      zkc.createNode(path,data);
      LOG.info("存入zookeeper，结点为：{}，数据为：{}",path,zkc.getData(path,new WatcherApi()));
    }
  }

  /**
   * 当链接关闭的时候
   * 这里没有做相关的代码处理
   * */
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    /*
     * 删除改用户，中断所有相关线程
     */
    String name = (String) session.getAttributes().get("name");
    List<Timer> allTask = allTasks.get(name);
    if (allTask != null){
      allTasks.remove(name);
      for (Timer timer : allTask){
        timer.cancel();//清空线程任务
      }
      String path = "/" + name;//生成zookeeper的存放路径
      zkc.deleteNode(path);
      LOG.info("删除zookeeper结点，结点为{}",path);
    }
    super.afterConnectionClosed(session, status);
    LOG.info("WebSocket连接断开！");
  }

}
