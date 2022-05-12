package com.qianmi.b2b.scheduling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <p>Date: 2022-03-22 17:39.</p>
 *
 * @author Administrator
 * @version 0.1.0
 */
@Slf4j
@SpringBootApplication
public class Starter {

    public static void main(String[] args) throws UnknownHostException {
        final Environment env = SpringApplication.run(Starter.class, args).getEnvironment();
        // @formatter:off
        log.info("Access URLs:\n" +
                        "----------------------------------------------------------\n" +
                        "\tLocal: \t\thttp://127.0.0.1:{}\n" +
                        "\tExternal: \thttp://{}:{}\n" +
                        "----------------------------------------------------------",
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
        // @formatter:on
    }
}
