package com.example.comtomize;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.apache.dubbo.rpc.protocol.grpc.interceptors.GrpcConfigurator;

import io.grpc.CallOptions;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Customize the gRPC Server, Channel and CallOptions
 */
public class MyGrpcConfigurator implements GrpcConfigurator {

    private final ExecutorService executor = Executors.newFixedThreadPool(200, new NamedThreadFactory("Customized-grpc", true));

    @Override
    public NettyServerBuilder configureServerBuilder(NettyServerBuilder builder, URL url) {
        return builder.flowControlWindow(1).executor(executor);
    }

    @Override
    public NettyChannelBuilder configureChannelBuilder(NettyChannelBuilder builder, URL url) {
        return builder.directExecutor();
    }

    @Override
    public CallOptions configureCallOptions(CallOptions options, URL url) {
        return options.withOption(CallOptions.Key.create("key"), "value");
    }
}
