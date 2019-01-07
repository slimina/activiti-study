package com.smartslim.activiti;

import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ActivitiConfig implements ProcessEngineConfigurationConfigurer {

    @Autowired
    ComActivitiEventListener comActivitiEventListener;

    @Override
    public void configure(SpringProcessEngineConfiguration springProcessEngineConfiguration) {
        //List<ActivitiEventListener> activitiEventListener=new ArrayList<ActivitiEventListener>();
        //activitiEventListener.add(comActivitiEventListener );//配置全局监听器
        //springProcessEngineConfiguration.setEventListeners(activitiEventListener);
        Map<String, List<ActivitiEventListener>> typedEventListeners = new HashMap<>();
    typedEventListeners.put(
        "PROCESS_STARTED,PROCESS_COMPLETED,PROCESS_CANCELLED",
        Arrays.asList(comActivitiEventListener));
        typedEventListeners.put("TASK_CREATED,TASK_ASSIGNED,TASK_COMPLETED", Arrays.asList(comActivitiEventListener));
        springProcessEngineConfiguration.setTypedEventListeners(typedEventListeners);
    }
}
