package com.eastcom.my_springboot_demo.websocket.intercepter;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * ClassName: WebSocketInterceptor <br/>
 * Function:  WebSocket的拦截器<br/>
 * Reason: WebSocket握手的拦截器，检查握手请求和响应，对WebSocketHandler传递属性，用于区别WebSocket<br/>
 * date: 2019/10/28 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
public class WebSocketInterceptor extends HttpSessionHandshakeInterceptor {

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    String url = request.getURI().toString();
    String name = url.substring(url.lastIndexOf("/") + 1);
    attributes.put("name",name);

    return super.beforeHandshake(request,response,wsHandler,attributes);
  }

  @Override
  public void afterHandshake(ServerHttpRequest request,ServerHttpResponse response,WebSocketHandler webSocketHandler,@Nullable Exception ex){
    super.afterHandshake(request,response,webSocketHandler,ex);
  }
}
