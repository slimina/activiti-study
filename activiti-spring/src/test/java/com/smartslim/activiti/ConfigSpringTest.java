package com.smartslim.activiti;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:activiti-context.xml"})
public class ConfigSpringTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigSpringTest.class);

    @Rule
    @Autowired
    public ActivitiRule activitiRule;

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Test
    @Deployment(resources = {"myprocess-spring.bpmn20.xml"})
    public void test() throws InterruptedException {
        activitiRule.getRuntimeService().startProcessInstanceByKey("myprocess");
        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        LOGGER.info("task = {}",task.getName());
        activitiRule.getTaskService().complete(task.getId());
    }
}
