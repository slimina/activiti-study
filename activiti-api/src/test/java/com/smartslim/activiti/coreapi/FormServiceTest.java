package com.smartslim.activiti.coreapi;

import com.google.common.collect.ImmutableMap;
import org.activiti.engine.FormService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 解析流程定义中表单项的配置
 * 提交表单的方式驱动用户节点流转
 * 获取自定义外部表单key
 *
 */
public class FormServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(FormServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"myprocess-form.bpmn20.xml"})
    public void testFormService(){
        FormService formService = activitiRule.getFormService();
        ProcessDefinition processDefinition = activitiRule.getRepositoryService().createProcessDefinitionQuery().singleResult();
        String startFormKey = formService.getStartFormKey(processDefinition.getId());
        LOGGER.info("startFormKey = > {}",startFormKey);

        StartFormData startFormData = formService.getStartFormData(processDefinition.getId());
        List<FormProperty> formProperties = startFormData.getFormProperties();
        formProperties.stream().map(ToStringBuilder::reflectionToString).forEach(System.err::println);

        //启动
        Map<String,String> properties = new HashMap<>();
        properties.put("message","start node!!!!");
        ProcessInstance processInstance = formService.submitStartFormData(processDefinition.getId(), properties);

        Task task = activitiRule.getTaskService().createTaskQuery().singleResult();
        TaskFormData taskFormData = formService.getTaskFormData(task.getId());
        List<FormProperty> formProperties1 = taskFormData.getFormProperties();
        formProperties1.stream().map(ToStringBuilder::reflectionToString).forEach(System.err::println);
        //流程指定往下
        formService.submitTaskFormData(task.getId(), ImmutableMap.of("yesORno","13115"));
        Task task1 = activitiRule.getTaskService().createTaskQuery().singleResult();
        System.err.println(task1);

    }
}
