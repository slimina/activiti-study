package com.smartslim.activiti.coreapi;

import org.activiti.engine.*;
import org.activiti.engine.delegate.BpmnError;

public class ActivitiExceptionTest {

    //继承ActivitiException --> RuntimeException
    ActivitiWrongDbException activitiWrongDbException;//引擎与数据库版本不匹配
    ActivitiOptimisticLockingException activitiOptimisticLockingException;//并发导致乐观锁异常
    ActivitiClassLoadingException activitiClassLoadingException;//加载类异常
    ActivitiObjectNotFoundException activitiObjectNotFoundException;//操作对象不存在
    ActivitiIllegalArgumentException activitiIllegalArgumentException;//非法的参数
    ActivitiTaskAlreadyClaimedException activitiTaskAlreadyClaimedException;//任务被重新声明代理人
    JobNotFoundException jobNotFoundException; // -->ActivitiObjectNotFoundException
    BpmnError bpmnError;//定义业务异常，控制流程
}
