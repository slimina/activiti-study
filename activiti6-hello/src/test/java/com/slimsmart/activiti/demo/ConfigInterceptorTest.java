package com.slimsmart.activiti.demo;

import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigInterceptorTest {

    private  static  final  Logger logger = LoggerFactory.getLogger(ConfigInterceptorTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_interceptor.cfg.xml");

    @Test
    @Deployment(resources = {"process/myprocess.bpmn20.xml"})
    public void test() {
        activitiRule.getRuntimeService().startProcessInstanceByKey("myprocess");
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        activitiRule.getTaskService().complete(task.getId());
    }

}
