package com.smartslim.activiti;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.springframework.stereotype.Component;

@Component
public class ComActivitiEventListener implements ActivitiEventListener {
    @Override
    public void onEvent(ActivitiEvent event) {
        System.err.println("-----"+event.getType());
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
