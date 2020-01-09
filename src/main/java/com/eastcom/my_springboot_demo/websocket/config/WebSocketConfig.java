package com.eastcom.my_springboot_demo.websocket.config;

import com.eastcom.my_springboot_demo.websocket.intercepter.WebSocketInterceptor;
import com.eastcom.my_springboot_demo.websocket.handler.TextMessageHandler;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * ClassName: WebSocketConfig <br/>
 * Function:  WebSocket配置类<br/>
 * Reason:  进行相关WebSocket配置<br/>
 * date: 2019/10/28 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */

@Configuration
@EnableWebSocket//启用WebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
    webSocketHandlerRegistry.addHandler(getHandler(),"/websocket/*").addInterceptors((HandshakeInterceptor) new WebSocketInterceptor()).setAllowedOrigins("*");
  }

  private WebSocketHandler getHandler() {
    return new TextMessageHandler();
  }
}
