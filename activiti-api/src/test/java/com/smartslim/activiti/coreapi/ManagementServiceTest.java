package com.smartslim.activiti.coreapi;

import com.fasterxml.jackson.core.JsonEncoding;
import com.smartslim.activiti.coreapi.mapper.MyCustomMapper;
import org.activiti.engine.ManagementService;
import org.activiti.engine.impl.cmd.AbstractCustomSqlExecution;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.util.json.JSONString;
import org.activiti.engine.management.TablePage;
import org.activiti.engine.runtime.DeadLetterJobQuery;
import org.activiti.engine.runtime.Job;
import org.activiti.engine.runtime.JobQuery;
import org.activiti.engine.runtime.SuspendedJobQuery;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.List;
import java.util.Map;

/**
 * 1.管理job任务
 * JobQuery查询一般的工作
 * TimerJobQuery 查询定时工作
 * SuspendedJobQuery 查询中断的工作
 * DeadLetterJobQuery 查询无法执行的工作
 * 2.数据库相关通用操作
 * 查询表结构元数据TableMateData
 * 通用表查询TablePageQuery
 * 执行自定义的Sql查询 executeCustomSql
 * 3.执行流程引擎命令（Commond）
 */
public class ManagementServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti-job.cfg.xml");

    @Test
    @Deployment(resources = "myprocess-job.bpmn20.xml")
    public  void testJobQuery(){
        ManagementService managementService = activitiRule.getManagementService();
        List<Job> jobs = managementService.createTimerJobQuery().listPage(0, 100);
        jobs.stream().map(ToStringBuilder::reflectionToString).forEach(System.err::println);

        JobQuery jobQuery = managementService.createJobQuery();
        SuspendedJobQuery suspendedJobQuery = managementService.createSuspendedJobQuery();
        DeadLetterJobQuery deadLetterJobQuery = managementService.createDeadLetterJobQuery();
    }

    @Test
    @Deployment(resources = "myprocess-job.bpmn20.xml")
    public  void testTablePageQuery(){
        ManagementService managementService = activitiRule.getManagementService();
        TablePage tablePage = managementService.createTablePageQuery().tableName(managementService.getTableName(ProcessDefinitionEntity.class))
                .listPage(0, 100);
        tablePage.getRows().forEach(System.err::println);
    }
    @Test
    @Deployment(resources = "myprocess.bpmn20.xml")
    public  void testCustomSQL(){
        activitiRule.getRuntimeService().startProcessInstanceByKey("myprocess");
        ManagementService managementService = activitiRule.getManagementService();

        List<Map<String, Object>> maps = managementService.executeCustomSql(new AbstractCustomSqlExecution<MyCustomMapper, List<Map<String, Object>>>(MyCustomMapper.class) {
            @Override
            public List<Map<String, Object>> execute(MyCustomMapper myCustomMapper) {
                return myCustomMapper.findAll();
            }
        });
        maps.forEach(System.err::println);
    }

    @Test
    @Deployment(resources = "myprocess.bpmn20.xml")
    public  void testCommand(){
        activitiRule.getRuntimeService().startProcessInstanceByKey("myprocess");
        ManagementService managementService = activitiRule.getManagementService();
        ProcessDefinitionEntity myprocess = managementService.executeCommand(new Command<ProcessDefinitionEntity>() {
            @Override
            public ProcessDefinitionEntity execute(CommandContext commandContext) {
                return commandContext.getProcessDefinitionEntityManager().findLatestProcessDefinitionByKey("myprocess");
            }
        });

        LOGGER.info("myprocess = > {}", myprocess);

    }
}
