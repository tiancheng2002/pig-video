package com.zhu;

import cn.hutool.core.util.RandomUtil;
import com.zhu.model.entity.User;
import com.zhu.service.IUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class VideoFrameworkApplicationTests {

//    @Resource
//    private IUserService userService;
//
//    @Test
//    void contextLoads() {
//        List<User> users = new ArrayList<>();
//        for (int i=0;i<2000;i++){
//            User user = new User();
//            user.setUserEmail("184953"+(1000+i)+"@qq.com");
//            user.setUserPassword("4532a9efe7f7028cca653d9f462ae526");
//            user.setUserNickname(RandomUtil.randomString(8));
//            user.setUserAvatar("https://p3-pc.douyinpic.com/aweme/100x100/aweme-avatar/tos-cn-avt-0015_0a69114a5280102312f509a571b00783.jpeg?from=2956013662");
//            users.add(user);
//        }
//        userService.saveBatch(users);
//    }

}
