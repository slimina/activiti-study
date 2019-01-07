package com.smartslim.activiti;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloBean {

  private static final Logger logger = LoggerFactory.getLogger(HelloBean.class);

    public void sayHello(){
        logger.info("------- {} ------","sayHello");
    }
}
