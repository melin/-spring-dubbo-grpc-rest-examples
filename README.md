基于dubbo3 triple协议，以及spring mvc，实现统一服务逻辑，支持三种不同调用模式：
1. dubbo client 适合内部跨系统和模块调用
2. Grpc client 实现底层引擎调用，例如统一元数据场景，应用层适合dubbo client 调用，如果spark 引擎层元数据，更适合Grpc client
3. Rest API 适合外部接口调用，不依赖 方便

#### 好处：
1. 基于protobuf IDL 定义接口，避免与具体实现绑定，相比基于swagger 生成接口api接口文档，更轻易，也不污染代码
2. 支持多种应用场景，
3. Grpc client 跨语言
4. 利用dubbo 服务治理能力。
5. ...

### IDL 定义
```protobuf
syntax = "proto3";

option java_multiple_files = true;
option java_package = "io.grpc.examples.helloworld";
option java_outer_classname = "HelloWorldProto";
option objc_class_prefix = "HLW";

package helloworld;

// The greeting service definition.
service Greeter {
  // Sends a greeting
  rpc sayHello (HelloRequest) returns (HelloReply) {}
}

// The request message containing the user's name.
message HelloRequest {
  string name = 1;
}

// The response message containing the greetings
message HelloReply {
  string message = 1;
}
```

### dubbo 服务
dubbo 服务端
```java
import io.grpc.examples.helloworld.DubboGreeterTriple;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;
import org.apache.dubbo.config.annotation.DubboService;

// GreeterImplBase 为 protobuf idl 生成代码
@DubboService()
public class GrpcGreeterImpl extends DubboGreeterTriple.GreeterImplBase {

    @Override
    public HelloReply sayHello(HelloRequest request) {
        System.out.println("Executing thread is " + Thread.currentThread().getName());
        return HelloReply.newBuilder().setMessage("Hello " + request.getName()).build();
    }
}
```
Dubbo 客户端
```java
@SpringBootApplication
@Service
@EnableDubbo
public class ConsumerApplication {

    @DubboReference
    private Greeter greeter;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ConsumerApplication.class, args);
        ConsumerApplication application = context.getBean(ConsumerApplication.class);
        String result = application.doSayHello("world");
        System.out.println("result: " + result);
    }

    public String doSayHello(String name) {
        return greeter.sayHello(HelloRequest.newBuilder().setName("world").build()).getMessage();
    }
}
```

### spring mvc controller
注册 Protobuf 消息 Converter
```java
@Bean
ProtobufJsonFormatHttpMessageConverter protobufHttpMessageConverter() {
    return new ProtobufJsonFormatHttpMessageConverter();
}
```

### 原生Grpc client 客户端实现
```java
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
```

```java
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
```

### 调用rest 接口
```shell
curl -X POST http://localhost:8181/hello \
   -H 'Content-Type: application/json' \
   -H 'Accept: application/json' \
   -d '{"name": World}'
```
