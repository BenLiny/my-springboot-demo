package com.eastcom.my_springboot_demo.kafka.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: Company <br/>
 * Function:  <br/>
 * Reason:  <br/>
 * date: 2019/12/2 <br/>
 *
 * @author lyh
 * @version 1.0.0
 * @since JDK 1.8
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {

  /**
   * name : 名字
   */
  private String name;

  /**
   * address : 地址
   */
  private String address;
}
