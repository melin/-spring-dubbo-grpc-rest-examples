package com.example;

import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/", consumes = {"application/json"}, produces = {"application/json"})
public class GrpcGreeterController {

    @Autowired
    private GrpcGreeterImpl greeter;

    @RequestMapping(value = "/hello")
    public HelloReply customer(@RequestBody HelloRequest request) {
        return greeter.sayHello(request);
    }
}
