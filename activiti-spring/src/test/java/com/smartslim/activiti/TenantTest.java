package com.smartslim.activiti;

import com.alibaba.fastjson.JSON;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 多租房测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations ={"classpath:applicationContext.xml"})
public class TenantTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantTest.class);

    @Rule
    @Autowired
    public ActivitiRule activitiRule;

    @Test
    public void testTenant() throws InterruptedException {
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        TaskService taskService = activitiRule.getTaskService();
        HistoryService historyService = activitiRule.getHistoryService();
        //部署
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.name("测试部署资源").tenantId("appkey1")
                .addClasspathResource("myprocess.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        LOGGER.info("deploy : {}", JSON.toJSONString(deploy));
        System.out.println("------------deploy-------------");
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();
        List<Deployment> deployments = deploymentQuery.deploymentTenantId("appkey1").list();
        deployments.stream().map(ToStringBuilder::reflectionToString).forEach(System.err::println);

        //查询流程定义
        System.out.println("------------ProcessDefinition-------------");
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().processDefinitionTenantId("appkey1").active().list();
        processDefinitions.stream().map(ToStringBuilder::reflectionToString).forEach(System.err::println);
        //流程实例
        System.out.println("------------ProcessInstance-------------");
        runtimeService.startProcessInstanceById(processDefinitions.iterator().next().getId());
        List<ProcessInstance> processInstances = runtimeService.createProcessInstanceQuery().processInstanceTenantId("appkey1").active().list();
        processInstances.stream().map(ToStringBuilder::reflectionToString).forEach(System.err::println);
        System.out.println("------------Execution-------------");
        List<Execution> executions = runtimeService.createExecutionQuery().executionTenantId("appkey1").list();
        executions.stream().map(ToStringBuilder::reflectionToString).forEach(System.err::println);
        System.out.println("------------Task-------------");
        List<Task> tasks = taskService.createTaskQuery().taskTenantId("appkey1").active().list();
        tasks.stream().map(ToStringBuilder::reflectionToString).forEach(System.err::println);
        System.out.println("------------HistoricProcessInstance-------------");
        taskService.complete(tasks.iterator().next().getId());
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().processInstanceTenantId("appkey1").list();
        historicProcessInstances.stream().map(ToStringBuilder::reflectionToString).forEach(System.err::println);

    }
}
