package com.slimsmart.activiti.demo.interceptor;

import org.activiti.engine.impl.interceptor.AbstractCommandInterceptor;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DurationCommandInterceptor extends AbstractCommandInterceptor {

  private static final Logger logger = LoggerFactory.getLogger(DurationCommandInterceptor.class);

    @Override
    public <T> T execute(CommandConfig config, Command<T> command) {
        Long time = System.currentTimeMillis();
        try{
            return this.getNext().execute(config,command);
        }finally{
            logger.info("{}执行时间：{} ms",command.getClass().getSimpleName(),System.currentTimeMillis() - time );
        }
    }
}
