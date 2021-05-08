package org.example.logs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@ShowParam
@RestController
public class TestController2 {

    @GetMapping("/test2/test")
    public String test(String userName, String password) {
        return "测试类注解" + userName + "-" + password;
    }

    @ShowParam(result = false,param = false,timestamp = false)
    @GetMapping("/test2/test01")
    public String test01(String userName, String password) {
        return "测试类注解" + userName + "-" + password;
    }

}
