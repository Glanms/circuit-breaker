# CircuitBreaker

> A simple CircuitBreaker


## Classes

- CircuitBreakerRunner 熔断器工具类
- CircuitBreaker 熔断器主要策略
- CircuitBreakerImpl 策略实现类
- CircuitBreakerConfig 配置参数，
- Status 熔断器状态。
    - open: 打开
    - half_open: sleep时间后测试打开
    - closed: 关闭