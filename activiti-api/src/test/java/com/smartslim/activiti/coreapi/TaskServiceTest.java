package com.smartslim.activiti.coreapi;

import com.google.common.collect.ImmutableMap;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.*;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 1.对用户任务（usertask）管理和流程的控制
 * task对象的创建、删除
 * 查询task、并驱动task节点完成执行
 * task相关参数变量的设置
 * 2.设置用户任务（userTask）的权限信息（拥有者、候选人、办理人）
 * 候选用户(candidateUser)和候选组(candidateGroup)
 * 指定拥有人(Owner)和办理人（assignee）
 * 通过claim设置办理人
 * 3.针对用户任务添加任务附件、评论和事件记录
 * 任务附件(Attachment)创建与查询
 * 任务评论(comment)创建和查询
 * 事件记录(event)创建和查询
 */
public class TaskServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    @Deployment(resources = {"myprocess-task.bpmn20.xml"})
    public void testTaskService(){
        Map<String,Object> variables = ImmutableMap.of("message","test message!!!!");
        activitiRule.getRuntimeService().startProcessInstanceByKey("myprocess",variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        LOGGER.info("task => {}", ToStringBuilder.reflectionToString(task, ToStringStyle.DEFAULT_STYLE));
        LOGGER.info("task desc : {}",task.getDescription());

        taskService.setVariable(task.getId(),"key1","vv1");
        //本task作用域可见
        taskService.setVariableLocal(task.getId(),"key2","vv2");

        taskService.getVariables(task.getId()).forEach((k,v)->{
            LOGGER.info(k+" ==> "+v);
        });
        taskService.getVariablesLocal(task.getId()).forEach((k,v)->{
            LOGGER.info(k+" --> "+v);
        });
        activitiRule.getRuntimeService().getVariables(task.getExecutionId()).forEach((k,v)->{
            LOGGER.info(k+" **> "+v);
        });

        taskService.complete(task.getId(),ImmutableMap.of("ckey","cvalue"));
        LOGGER.info("variables = {}",activitiRule.getRuntimeService().getVariables(task.getExecutionId()));
    }

    @Test
    @Deployment(resources = {"myprocess-task.bpmn20.xml"})
    public void testTaskServiceUser(){
        Map<String,Object> variables = ImmutableMap.of("message","test message!!!!");
        activitiRule.getRuntimeService().startProcessInstanceByKey("myprocess",variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        LOGGER.info("task => {}", ToStringBuilder.reflectionToString(task, ToStringStyle.DEFAULT_STYLE));
        LOGGER.info("task desc : {}",task.getDescription());
        taskService.setOwner(task.getId(),"user1");
        //taskService.setAssignee(task.getId(),"jimmy");
        List<Task> taskList = taskService.createTaskQuery().taskCandidateUser("jimmy").taskUnassigned().listPage(0, 100);
        taskList.forEach(t->{
           try{
               taskService.claim(task.getId(),"jimmy");
               //taskService.unclaim(task.getId());
           }catch (Exception e){
                e.printStackTrace();
           }
        });
        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
        identityLinksForTask.forEach(i->{
            LOGGER.info(" identityLink = {}",i);
        });

        List<Task> jimmys = taskService.createTaskQuery().taskAssignee("jimmy").listPage(0, 100);
        jimmys.forEach(e->{
            taskService.complete(e.getId(),ImmutableMap.of("ckey1","cvalue1"));
        });
        Assert.assertTrue(taskService.createTaskQuery().taskAssignee("jimmy").listPage(0, 100).size() == 0);
    }

  @Test
  @Deployment(resources = {"myprocess-task.bpmn20.xml"})
  public void testTaskAttachment() {
        Map<String, Object> variables = ImmutableMap.of("message", "test message!!!!");
      activitiRule.getRuntimeService().startProcessInstanceByKey("myprocess", variables);
      TaskService taskService = activitiRule.getTaskService();
      Task task = taskService.createTaskQuery().singleResult();
      Attachment attachment = taskService.createAttachment("url", task.getId(), task.getProcessInstanceId(), "图片", "发票", "http://164654564");
      LOGGER.info("attachment : {}",ToStringBuilder.reflectionToString(attachment,ToStringStyle.DEFAULT_STYLE));
      List<Attachment> taskAttachments = taskService.getTaskAttachments(task.getId());
      taskAttachments.forEach(a->{
          LOGGER.info("taskAttachment : {}",ToStringBuilder.reflectionToString(a,ToStringStyle.DEFAULT_STYLE));
      });
  }

    @Test
    @Deployment(resources = {"myprocess-task.bpmn20.xml"})
    public void testTaskComment() {
        Map<String, Object> variables = ImmutableMap.of("message", "test message!!!!");
        activitiRule.getRuntimeService().startProcessInstanceByKey("myprocess", variables);
        TaskService taskService = activitiRule.getTaskService();
        Task task = taskService.createTaskQuery().singleResult();
        taskService.setOwner(task.getId(),"user1");
        taskService.setAssignee(task.getId(),"jimmy");
        taskService.setAssignee(task.getId(),"tom");
        taskService.addComment(task.getId(),task.getProcessInstanceId(),"message1231230");
        taskService.addComment(task.getId(),task.getProcessInstanceId(),"test125412");

        List<Comment> taskComments = taskService.getTaskComments(task.getId());
        taskComments.forEach(a->{
            LOGGER.info("taskComment : {}",ToStringBuilder.reflectionToString(a,ToStringStyle.DEFAULT_STYLE));
        });
        List<Event> taskEvents = taskService.getTaskEvents(task.getId());
        taskEvents.forEach(a->{
            LOGGER.info("taskEvent : {}",ToStringBuilder.reflectionToString(a,ToStringStyle.DEFAULT_STYLE));
        });

    }
}
