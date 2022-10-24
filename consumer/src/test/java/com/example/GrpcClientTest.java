package com.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GrpcClientTest {
    private static GreeterGrpc.GreeterStub stub;
    private static GreeterGrpc.GreeterBlockingStub blockingStub;

    @BeforeClass
    public static void init() {
        final ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 30001)
                .usePlaintext()
                .build();
        stub = GreeterGrpc.newStub(channel);
        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    @Test
    public void unaryGreeter() {
        final HelloReply reply = blockingStub.sayHello(HelloRequest.newBuilder().setName("World").build());
        Assert.assertEquals("Hello World", reply.getMessage());
    }
}
