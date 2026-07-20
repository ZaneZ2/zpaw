# Coding Conventions

## Author

- 所有新增 Java 类添加 `@author Zane` Javadoc 标签。
- 所有新增枚举、方法、关键代码块添加有意义的中文 Javadoc 注释。

## Java 注释规范

- **类注释**：说明类的职责、使用场景、与上下游的关系。
- **枚举注释**：每个枚举值上方添加 `/** ... */` 说明含义和触发时机。
- **方法注释**：public 方法必须加 Javadoc，说明参数、返回值、异常。
- **关键代码块**：复杂逻辑、特殊处理、线程调度、超时等需加行内注释。
- **非 public 方法**：如果逻辑不直观也加注释。

## API 响应规范

- SSE 流式事件：必须使用 `StreamEvent` 枚举 + `record` 类型，经 Jackson 序列化。
- REST JSON 响应：使用 `Map.of()` / `List.of()` 等 Spring Boot 自动序列化的方式。
- 禁止手动拼接 JSON 字符串（`"data: {\"type\":\"..."`）。
