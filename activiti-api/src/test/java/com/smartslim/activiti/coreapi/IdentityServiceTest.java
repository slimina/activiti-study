package com.smartslim.activiti.coreapi;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.util.json.JSONObject;
import org.activiti.engine.test.ActivitiRule;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 1.用户管理
 * 2.管理用户组
 * 3.用户和用户组的关系 membership
 *
 * IdentityService.saveUser
 * --CommandExecutor.execute(SaveUserCmd) //CommandInterceptor
 *   -- UserEntityManager.insert
 *     -- UserDataManager:MybatisUserDataManager.insert
 *        -- DbSqlSession.insert
 */
public class IdentityServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentityServiceTest.class);

    @Rule
    public ActivitiRule activitiRule = new ActivitiRule();

    @Test
    public void testIdentity(){
        IdentityService identityService = activitiRule.getIdentityService();
        User user1 = identityService.newUser("user1");
        user1.setEmail("user1@163.com");
        identityService.saveUser(user1);
        User user2 = identityService.newUser("user2");
        user2.setEmail("user2@163.com");
        identityService.saveUser(user2);

        Group group1 = identityService.newGroup("group1");
        identityService.saveGroup(group1);
        Group group2 = identityService.newGroup("group2");
        identityService.saveGroup(group2);

        identityService.createMembership(user1.getId(),group1.getId());
        identityService.createMembership(user2.getId(),group1.getId());
        identityService.createMembership(user1.getId(),group2.getId());

        User user = identityService.createUserQuery().userId(user1.getId()).singleResult();
        user.setFirstName("tom");
        identityService.saveUser(user);//version  版本增加
        List<User> list = identityService.createUserQuery().memberOfGroup(group1.getId()).listPage(0, 100);
        list.stream().map(ToStringBuilder::reflectionToString).forEach(System.err::println);
        List<Group> groups = identityService.createGroupQuery().groupMember(user1.getId()).listPage(0, 100);
        groups.stream().map(ToStringBuilder::reflectionToString).forEach(System.err::println);

    }

}
