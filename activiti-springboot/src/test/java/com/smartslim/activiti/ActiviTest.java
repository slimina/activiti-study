package com.smartslim.activiti;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class ActiviTest {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;


    @Test
    public void test(){
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myprocess");
        Task task = taskService.createTaskQuery().singleResult();
        taskService.complete(task.getId());
    }
}
