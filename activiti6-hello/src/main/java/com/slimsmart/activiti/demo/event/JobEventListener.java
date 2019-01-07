package com.slimsmart.activiti.demo.event;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.engine.delegate.event.ActivitiEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobEventListener implements ActivitiEventListener{

    private static  final Logger logger = LoggerFactory.getLogger(JobEventListener.class);

    @Override
    public void onEvent(ActivitiEvent event) {
        ActivitiEventType eventType = event.getType();
        String name = eventType.name();
        if(name.startsWith("TIMER") || name.startsWith("JOB")){
            logger.info("监听到job时间{} \t {}",eventType,event.getProcessDefinitionId());
        }
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
