package org.example.logs;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试控制层
 *
 * @author 李昆城
 */
@RestController
public class TestController {

    @GetMapping("/test")
    @ShowParam
    public String test() {
        return "无参数，全部展示";
    }

    @GetMapping("/test01")
    @ShowParam(dataPersistence = true)
    public String test01(String userName, String password) {
        return "有参数，全部展示" + userName + "-" + password;
    }

    @GetMapping("test02")
    @ShowParam(result = false)
    public String test02(String userName, String password) {
        return "有参数，展示参数，不展示返回值 " + userName + "-" + password;
    }

    @GetMapping("/test03")
    @ShowParam(param = false)
    public String test03(String userName, String password) {
        return "有参数，展示返回值，不展示参数" + userName + "-" + password;
    }

    @GetMapping("test04")
    @ShowParam(result = false, param = false)
    public String test04(String userName, String password) {
        return "有参数，全部不展示";
    }


}
