package com.slimsmart.activiti.demo;

import com.google.common.collect.Maps;
import org.activiti.engine.*;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.form.DateFormType;
import org.activiti.engine.impl.form.StringFormType;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DemoMain {

  private static final Logger logger = LoggerFactory.getLogger(DemoMain.class);

  public static void main(String[] args) throws Exception {
    logger.info("启动程序");
    // 创建流程引擎
    ProcessEngine processEngine = getProcessEngine();
    // 部署流程定义文件
    ProcessDefinition processDefinition = getProcessDefinition(processEngine);
    // 启动运行流程

    RuntimeService runtimeService = processEngine.getRuntimeService();
    ProcessInstance processInstance =
        runtimeService.startProcessInstanceById(processDefinition.getId());
    logger.info("流程业务key:{}", processInstance.getProcessDefinitionKey());

    TaskService taskService = processEngine.getTaskService();
    FormService formService = processEngine.getFormService();
    // 处理流程任务
    Scanner scanner = new Scanner(System.in);
    while (processInstance != null && !processInstance.isEnded()) {
      List<Task> list = taskService.createTaskQuery().list();
      logger.info("待处理任务数量 [{}]", list.size());
      for (Task task : list) {
        logger.info("待处理任务名称:{}", task.getName());
        Map<String, Object> variables = null;
        try {
          variables = buildVariablesByScanner(scanner, task, formService);
          taskService.complete(task.getId(), variables);
          processInstance =
              runtimeService
                  .createProcessInstanceQuery()
                  .processInstanceId(processInstance.getId())
                  .singleResult();
        } catch (ParseException e) {
          e.printStackTrace();
        }
      }
    }

    scanner.close();
    logger.info("关闭程序");
  }

  private static Map<String, Object> buildVariablesByScanner(
      Scanner scanner, Task task, FormService formService) throws ParseException {
    TaskFormData taskFormData = formService.getTaskFormData(task.getId());
    List<FormProperty> formProperties = taskFormData.getFormProperties();
    return buildVariablesByScanner(scanner, formProperties);
  }

  public static Map<String, Object> buildVariablesByScanner(
      Scanner scanner, List<FormProperty> formProperties) throws ParseException {
    Map<String, Object> variables = Maps.newHashMap();
    for (FormProperty property : formProperties) {
      String line = null;
      if (StringFormType.class.isInstance(property.getType())) {
        logger.info("请输入 {} ？", property.getName());
        line = scanner.nextLine();
        variables.put(property.getId(), line);
      } else if (DateFormType.class.isInstance(property.getType())) {
        logger.info("请输入 {} ？ 格式 （yyyy-MM-dd）", property.getName());
        line = scanner.nextLine();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(line);
        variables.put(property.getId(), date);
      } else {
        logger.info("类型暂不支持 {}", property.getType());
      }
      logger.info("您输入的内容是 [{}]", line);
    }
    return variables;
  }

  private static ProcessDefinition getProcessDefinition(ProcessEngine processEngine) {
    RepositoryService repositoryService = processEngine.getRepositoryService();
    DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
    deploymentBuilder.addClasspathResource("demo.bpmn");
    Deployment deployment = deploymentBuilder.deploy();
    String deploymentId = deployment.getId();
    ProcessDefinition processDefinition =
        repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId).singleResult();
    logger.info(
        "流程名称:{},流程Id：{},流程key:{}",
        processDefinition.getName(),
        processDefinition.getId(),
        processDefinition.getKey());
    return processDefinition;
  }

  private static ProcessEngine getProcessEngine() {
    ProcessEngineConfiguration cfg =
        ProcessEngineConfiguration.createStandaloneInMemProcessEngineConfiguration();
    ProcessEngine processEngine = cfg.buildProcessEngine();
    String name = processEngine.getName();
    String version = ProcessEngine.VERSION;
    logger.info("流程引擎名称：{}", name);
    logger.info("流程引擎版本：{}", version);
    return processEngine;
  }
}
