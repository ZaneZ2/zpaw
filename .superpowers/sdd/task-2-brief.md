# Task 2: 搭建后端模块结构（骨架）

## Files to Create

- `src/main/java/cn/zane/bootstrap/ZPawBootstrap.java`
- `src/main/java/cn/zane/bootstrap/config/ApplicationConfig.java`
- `src/main/java/cn/zane/bootstrap/config/ModelConfig.java`
- `src/main/java/cn/zane/agent/config/ZPawAgentConfig.java`
- `src/main/java/cn/zane/agent/factory/ZPawAgentFactory.java`
- `src/main/java/cn/zane/web/config/SecurityConfig.java`
- `src/main/java/cn/zane/web/config/WebConfig.java`
- `src/main/java/cn/zane/knowledge/controller/KnowledgeController.java`
- `src/main/java/cn/zane/knowledge/service/KnowledgeService.java`
- `src/main/java/cn/zane/workflow/controller/WorkflowController.java`
- `src/main/java/cn/zane/workflow/service/WorkflowService.java`

## Context

This is Phase 1 base scaffolding for the zpaw personal agent project. The project uses:

- Java 21, Spring Boot 4.1+ (WebFlux reactive stack)
- AgentScope Java 2.0.0 (Maven Central)
- Package root: `cn.zane`
- Build with Maven

All files are STUB implementations — they compile and run but have minimal or no real logic. Phase 2 will add real implementations.

## Code to Write

### ZPawBootstrap.java

```java
package cn.zane.bootstrap;

import io.agentscope.harness.agent.HarnessAgent;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ZPawBootstrap {
    private static final Logger log = LoggerFactory.getLogger(ZPawBootstrap.class);
    private HarnessAgent defaultAgent;

    @PostConstruct
    public void init() {
        log.info("ZPawBootstrap initialized — agent scaffolding ready");
    }

    public HarnessAgent getDefaultAgent() { return defaultAgent; }
}
```

### ApplicationConfig.java

```java
package cn.zane.bootstrap.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    // Phase 2+ 逐步添加 Bean
}
```

### ModelConfig.java

```java
package cn.zane.bootstrap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "zpaw.model")
public class ModelConfig {
    private String defaultModel = "dashscope:qwen-plus";
    private List<ModelEntry> providers = new ArrayList<>();

    public String getDefaultModel() { return defaultModel; }
    public void setDefaultModel(String v) { this.defaultModel = v; }
    public List<ModelEntry> getProviders() { return providers; }
    public void setProviders(List<ModelEntry> v) { this.providers = v; }

    public static class ModelEntry {
        private String id;
        private String apiKey;
        private String modelName;
        private boolean stream = true;

        public String getId() { return id; }
        public void setId(String v) { this.id = v; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String v) { this.apiKey = v; }
        public String getModelName() { return modelName; }
        public void setModelName(String v) { this.modelName = v; }
        public boolean isStream() { return stream; }
        public void setStream(boolean v) { this.stream = v; }
    }
}
```

### ZPawAgentConfig.java (stub)

```java
package cn.zane.agent.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ZPawAgentConfig {
    // Phase 2: Agent configuration
}
```

### ZPawAgentFactory.java (stub)

```java
package cn.zane.agent.factory;

import org.springframework.stereotype.Component;

@Component
public class ZPawAgentFactory {
    // Phase 2: Create HarnessAgent instances
}
```

### SecurityConfig.java

```java
package cn.zane.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(e -> e.anyExchange().permitAll())
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .build();
    }
}
```

### WebConfig.java

```java
package cn.zane.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class WebConfig implements WebFluxConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }
}
```

### KnowledgeController.java (stub)

```java
package cn.zane.knowledge.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    @PostMapping("/upload")
    public Mono<String> upload() {
        return Mono.just("{\"status\":\"not_implemented\"}");
    }

    @PostMapping("/search")
    public Mono<String> search() {
        return Mono.just("{\"status\":\"not_implemented\"}");
    }
}
```

### KnowledgeService.java (stub)

```java
package cn.zane.knowledge.service;

import org.springframework.stereotype.Service;

@Service
public class KnowledgeService {
    // Phase 3: RAG knowledge base implementation
}
```

### WorkflowController.java (stub)

```java
package cn.zane.workflow.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {
    @GetMapping
    public Mono<String> list() {
        return Mono.just("[]");
    }
}
```

### WorkflowService.java (stub)

```java
package cn.zane.workflow.service;

import org.springframework.stereotype.Service;

@Service
public class WorkflowService {
    // Phase 4: Workflow engine implementation
}
```

## Verification

After creating all files, verify compilation:

```
mvn clean compile -DskipTests
```

Expected: BUILD SUCCESS
