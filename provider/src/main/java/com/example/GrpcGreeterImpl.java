package com.example;

import io.grpc.examples.helloworld.DubboGreeterTriple;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService()
public class GrpcGreeterImpl extends DubboGreeterTriple.GreeterImplBase {

    @Override
    public HelloReply sayHello(HelloRequest request) {
        System.out.println("Executing thread is " + Thread.currentThread().getName());
        return HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
    }
}
