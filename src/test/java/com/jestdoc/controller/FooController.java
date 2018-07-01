package com.jestdoc.controller;

import com.jestdoc.vo.User;
import com.jestdoc.web.Response;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/foo")
public class FooController {

    /**
     * echo
     *
     * @param name 用户名
     * @return echo response
     * @sample name foo
     */
    @GetMapping("/echo")
    public Response<?> echo(@RequestParam("name") String name) {
        return new Response(200, "hello " + name);
    }


    /**
     * 用户注册
     *
     * @param user 用户
     * @return 注册结果
     * @sample user {firstName:foo, middleName:bar, lastName: baz}
     */
    @PostMapping("/register")
    public Response<User> register(@RequestBody User user) {
        return new Response<>();
    }

    /**
     * 获取用户信息
     *
     * @param id 用户ID
     * @return 用户信息
     * @sample id 1
     */
    @GetMapping("/u/{id}")
    public Response<User> user(@PathVariable("id") String id) {
        return new Response<>();
    }

}
