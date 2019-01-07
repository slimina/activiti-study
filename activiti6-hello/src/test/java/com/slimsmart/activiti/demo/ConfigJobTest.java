package com.slimsmart.activiti.demo;

import org.activiti.engine.runtime.Job;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.ActivitiRule;
import org.activiti.engine.test.Deployment;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ConfigJobTest {

    private  static  final  Logger logger = LoggerFactory.getLogger(ConfigJobTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule("activiti_job.cfg.xml");

    @Test
    @Deployment(resources = {"process/myprocess-job.bpmn20.xml"})
    public void test() throws InterruptedException {
        logger.info("开始");
        //查询当前定时任务
        List<Job> jobs = activitiRule.getManagementService().createTimerJobQuery().listPage(0, 100);
        jobs.forEach(job->{
            logger.info("定时任务{}，默认重试次数{}",job,job.getRetries());
        });
        logger.info("jobList size : {}",jobs.size());
        Thread.sleep(1000*100);
        logger.info("结束");
    }

}
