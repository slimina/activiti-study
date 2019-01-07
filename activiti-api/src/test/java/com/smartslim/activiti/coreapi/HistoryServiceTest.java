package com.smartslim.activiti.coreapi;

import com.google.common.collect.ImmutableMap;
import org.activiti.engine.HistoryService;
import org.activiti.engine.history.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 作用：
 * 1.管理流程实例结束后的历史实例
 * 2.构建历史数据的查询对象
 * 3.根据流程实例id删除流程历史数据
 *
 * HistoricProcessInstance 历史流程实例实体类
 * HistoricVariableInstance 流程或任务变量值的实体
 * HistoricActivityInstance 单个活动节点执行信息
 * HistoricTaskInstance 用户任务实例的信息
 * HistoricDetail    历史流程活动任务详细信息
 *
 * 方法：
 * create[历史数据实体]Query
 * createNative[历史数据实体]Query
 * createProcessInstanceHistoeyLogQuery
 * deleteHistoricProcessInstance
 * delleteHistoricTaskInstance
 *
 */
public class HistoryServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-history.cfg.xml");

    @Test
    @Deployment(resources = "myprocess.bpmn20.xml")
    public void testHistory(){
        HistoryService historyService = activitiRule.getHistoryService();
        ProcessInstanceBuilder processInstanceBuilder = activitiRule.getRuntimeService().createProcessInstanceBuilder();
        ProcessInstance processInstance = processInstanceBuilder.processDefinitionKey("myprocess")
                .variables(ImmutableMap.of("key1", "value1", "key2", "value2", "key3", "value3"))
                .transientVariables(ImmutableMap.of("tkey", "tvalue")).start();
        activitiRule.getRuntimeService().setVariable(processInstance.getId(),"key1","vvvv");

        Task task = activitiRule.getTaskService().createTaskQuery().processDefinitionId(processInstance
                .getProcessDefinitionId()).active().singleResult();
        //activitiRule.getTaskService().complete(task.getId(),variables);
        activitiRule.getFormService().submitTaskFormData(task.getId(),ImmutableMap.of("fkey1","fvalue1","key2","value___"));


        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery().listPage(0, 100);
        historicProcessInstances.stream().map(ToStringBuilder::reflectionToString).forEach(LOGGER::info);

        List<HistoricActivityInstance> historicActivityInstances = historyService.createHistoricActivityInstanceQuery().listPage(0, 100);
        historicActivityInstances.stream().map(ToStringBuilder::reflectionToString).forEach(LOGGER::info);

        List<HistoricTaskInstance> historicTaskInstances = historyService.createHistoricTaskInstanceQuery().listPage(0, 100);
        historicTaskInstances.stream().map(ToStringBuilder::reflectionToString).forEach(LOGGER::info);

        List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery().listPage(0, 100);
        historicVariableInstances.stream().map(ToStringBuilder::reflectionToString).forEach(LOGGER::info);

        List<HistoricDetail> historicDetails = historyService.createHistoricDetailQuery().listPage(0, 100);
        historicDetails.stream().map(ToStringBuilder::reflectionToString).forEach(LOGGER::info);
        ProcessInstanceHistoryLog processInstanceHistoryLog = historyService.createProcessInstanceHistoryLogQuery(processInstance.getId()).includeActivities()
                .includeComments().includeFormProperties().includeTasks().includeVariables().includeVariableUpdates().singleResult();
        processInstanceHistoryLog.getHistoricData().stream().map(ToStringBuilder::reflectionToString).forEach(LOGGER::info);
        System.err.println("--------");
        //删除实例所有历史数据
        historyService.deleteHistoricProcessInstance(processInstance.getId());
        historicActivityInstances = historyService.createHistoricActivityInstanceQuery().listPage(0, 100);
        historicActivityInstances.stream().map(ToStringBuilder::reflectionToString).forEach(LOGGER::info);
    }
}
