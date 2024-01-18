package com.shiyuan.crawler.server;

import com.shiyuan.crawler.server.utils.ThreadPoolUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@SpringBootApplication
public class CrawlerServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrawlerServerApplication.class, args);
    }

    @Scheduled(cron = "* * * * * *")
    public void task(){
        ThreadPoolUtils.printJosQueue();
    }

}
