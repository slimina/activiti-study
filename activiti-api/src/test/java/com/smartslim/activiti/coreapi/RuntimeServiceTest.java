package com.smartslim.activiti.coreapi;

import com.google.common.collect.Maps;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceBuilder;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 启动流程及对流程数据的控制
 * 流程实例和执行流的查询
 * 触发流程操作 接收消息 和 信号
 *
 * 启动流程的方式（ID，key，message）
 * 启动可选参数 businesskey variables tenantid
 *
 * 流程实例： 一次工作流业务的数据实体 接口继承与执行流
 * 执行流： 流程实例中具体的执行路径
 *
 */
public class RuntimeServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuntimeServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"myprocess.bpmn20.xml"})
    public void testStartProcerss(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String,Object> variables = Maps.newHashMap();
        variables.put("key1","value1");
        ProcessInstance myprocess = runtimeService.startProcessInstanceByKey("myprocess",variables);
        //runtimeService.startProcessInstanceById() repositoryService查询流程定义id
        LOGGER.info("myprocess => {}",myprocess);
    }

    @Test
    @Deployment(resources = {"myprocess.bpmn20.xml"})
    public void testStartProcessInstanceBuilder(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String,Object> variables = Maps.newHashMap();
        variables.put("key1","value1");
        ProcessInstanceBuilder processInstanceBuilder = runtimeService.createProcessInstanceBuilder();
        //processInstanceBuilder.tenantId()//多租房
        ProcessInstance processInstance = processInstanceBuilder.businessKey("业务key").processDefinitionKey("myprocess")
                .variables(variables).start();
        LOGGER.info("myprocess => {}",processInstance);
    }

    @Test
    @Deployment(resources = {"myprocess.bpmn20.xml"})
    public void testVariables(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String,Object> variables = Maps.newHashMap();
        variables.put("key1","value1");
        variables.put("key2","value2");
        ProcessInstance myprocess = runtimeService.startProcessInstanceByKey("myprocess",variables);
        LOGGER.info("myprocess => {}",myprocess);
        runtimeService.setVariable(myprocess.getId(),"key3","value3");
        runtimeService.setVariable(myprocess.getId(),"key2","vvvvvvv");
        Map<String, Object> variables1 = runtimeService.getVariables(myprocess.getId());
        LOGGER.info("variables1 => {}",variables1);

    }

    @Test
    @Deployment(resources = {"myprocess.bpmn20.xml"})
    public void testProcessInstanceQuery(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String,Object> variables = Maps.newHashMap();
        variables.put("key1","value1");
        ProcessInstance myprocess = runtimeService.startProcessInstanceByKey("myprocess",variables);
        LOGGER.info("myprocess => {}",myprocess);

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(myprocess.getProcessInstanceId()).singleResult();
        LOGGER.info("processInstance => {}",processInstance);
    }

    @Test
    @Deployment(resources = {"myprocess.bpmn20.xml"})
    public void testExecutionQuery(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        Map<String,Object> variables = Maps.newHashMap();
        variables.put("key1","value1");
        ProcessInstance myprocess = runtimeService.startProcessInstanceByKey("myprocess",variables);
        LOGGER.info("myprocess => {}",myprocess);
        //执行流对象
        List<Execution> executions = runtimeService.createExecutionQuery().processDefinitionKey("myprocess").listPage(0,100);
        executions.forEach(e->{
            LOGGER.info("execution => {}",e);
        });
    }

    @Test
    @Deployment(resources = {"myprocess-trigger.bpmn20.xml"})
    public void testTrigger(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance myprocess = runtimeService.startProcessInstanceByKey("myprocess");
        Execution execution = runtimeService.createExecutionQuery().activityId("userTask").singleResult();
        LOGGER.info("execution => {}",execution);
        runtimeService.trigger(execution.getId());
        execution = runtimeService.createExecutionQuery().activityId("userTask").singleResult();
        LOGGER.info("execution => {}",execution);
    }

    @Test
    @Deployment(resources = {"myprocess-signal-received.bpmn20.xml"})
    public void testSignalEventReceived(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance myprocess = runtimeService.startProcessInstanceByKey("myprocess");
        Execution execution = runtimeService.createExecutionQuery().signalEventSubscriptionName("my-signal").singleResult();
        LOGGER.info("execution => {}",execution);
        runtimeService.signalEventReceived("my-signal");
        execution = runtimeService.createExecutionQuery().signalEventSubscriptionName("my-signal").singleResult();
        LOGGER.info("execution => {}",execution);
    }

    @Test
    @Deployment(resources = {"myprocess-message-received.bpmn20.xml"})
    public void testMessageEventReceived(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance myprocess = runtimeService.startProcessInstanceByKey("myprocess");
        Execution execution = runtimeService.createExecutionQuery().messageEventSubscriptionName("my-message").singleResult();
        LOGGER.info("execution => {}",execution);
        runtimeService.messageEventReceived("my-message",execution.getId());
        execution = runtimeService.createExecutionQuery().messageEventSubscriptionName("my-message").singleResult();
        LOGGER.info("execution => {}",execution);
    }

    @Test
    @Deployment(resources = {"myprocess-message-start.bpmn20.xml"})
    public void testMessageStart(){
        RuntimeService runtimeService = activitiRule.getRuntimeService();
        ProcessInstance myprocess = runtimeService.startProcessInstanceByMessage("my-message");
        List<Execution> executions = runtimeService.createExecutionQuery().listPage(0,100);
        executions.forEach(e->{
            LOGGER.info("execution => {}",e);
        });
    }
}
