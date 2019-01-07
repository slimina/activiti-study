package com.smartslim.activiti.coreapi.mapper;

import org.activiti.engine.task.Task;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface MyCustomMapper {

    @Select("SELECT * FROM ACT_RU_TASK")
    List<Map<String,Object>> findAll();
}
