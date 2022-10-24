验证统一服务实现，同时支持dubbo、原生Grpc、以及rest api能力

### 方法rest 接口
```shell
curl -X POST http://localhost:8181/hello \
   -H 'Content-Type: application/json' \
   -H 'Accept: application/json' \
   -d '{"name": World}'
```
