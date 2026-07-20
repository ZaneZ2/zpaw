package cn.zane;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ZPaw 应用主入口。
 *
 * <p>Spring Boot 启动类，负责启动整个后端服务。</p>
 *
 * @author Zane
 */
@SpringBootApplication
public class ZPaw {

    /**
     * 应用主入口方法。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ZPaw.class, args);
    }
}
