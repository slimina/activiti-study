package com.smartslim.activiti.coreapi;

import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.test.ActivitiRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * RepositoryService 流程部署  定义
 */
public class RepositoryServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void test(){
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.name("测试部署资源1").tenantId("多租户")
                .addClasspathResource("demo.bpmn20.xml")
                .addClasspathResource("myprocess.bpmn20.xml");
        Deployment deploy = deploymentBuilder.deploy();
        LOGGER.info("deploy : {}",deploy);
        deploymentBuilder = repositoryService.createDeployment();
        deploymentBuilder.name("测试部署资源2")
                .addClasspathResource("demo.bpmn20.xml")
                .addClasspathResource("myprocess.bpmn20.xml");
        deploy = deploymentBuilder.deploy();
        LOGGER.info("deploy : {}",deploy);
        DeploymentQuery deploymentQuery = repositoryService.createDeploymentQuery();

        List<Deployment> deployments = deploymentQuery.orderByDeploymenTime().asc().listPage(0, 100);
        deployments.forEach(System.err::println);
        //一次部署多个文件
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
                .orderByDeploymentId().asc().listPage(0, 100);
        processDefinitions.forEach(System.err::println);

    }


    /**
     * 流程定义暂停 不能被启用
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = {"myprocess.bpmn20.xml"})
    public void testSuspend(){
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        ProcessDefinition  processDefinition= repositoryService.createProcessDefinitionQuery().singleResult();
        LOGGER.info("processDefinition  = {}",processDefinition);
        repositoryService.suspendProcessDefinitionById(processDefinition.getId());
        try{
            activitiRule.getRuntimeService().startProcessInstanceById(processDefinition.getId());
        }catch (Exception e){
            e.printStackTrace();
        }finally{

        }

        repositoryService.activateProcessDefinitionById(processDefinition.getId());
        ProcessInstance processInstance = activitiRule.getRuntimeService().startProcessInstanceById(processDefinition.getId());
        LOGGER.info("processInstance = {}",processInstance);
    }

    /**
     *  指定用户或者用户组启动流程
     */
    @Test
    @org.activiti.engine.test.Deployment(resources = {"myprocess.bpmn20.xml"})
    public void testCandidateStarter(){
        RepositoryService repositoryService = activitiRule.getRepositoryService();
        ProcessDefinition  processDefinition= repositoryService.createProcessDefinitionQuery().singleResult();
        LOGGER.info("processDefinition  = {}",processDefinition);
        repositoryService.addCandidateStarterUser(processDefinition.getId(),"user");
        repositoryService.addCandidateStarterGroup(processDefinition.getId(),"groupM");
        //获取绑定关系、业务代码校验
        List<IdentityLink> identityLinksForProcessDefinition = repositoryService.getIdentityLinksForProcessDefinition(processDefinition.getId());
        identityLinksForProcessDefinition.forEach(System.err::println);
        //删除
        repositoryService.deleteCandidateStarterGroup(processDefinition.getId(),"groupM");
        identityLinksForProcessDefinition = repositoryService.getIdentityLinksForProcessDefinition(processDefinition.getId());
        identityLinksForProcessDefinition.forEach(System.err::println);


    }

}
