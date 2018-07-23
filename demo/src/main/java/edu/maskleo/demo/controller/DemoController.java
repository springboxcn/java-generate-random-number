package edu.maskleo.demo.controller;

import edu.maskleo.demo.config.RedisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/")
public class DemoController {

    public static volatile boolean go = true;

    static ExecutorService executorService = Executors.newFixedThreadPool(10);

    @Autowired
    private RedisConfig redisConfig;

    @ResponseBody
    @RequestMapping("/")
    public String index() {
        Thread s = new Thread(new TimerThread());
        s.start();
        Thread thread = new Thread(() -> {
            while (go) {
                // executorService.execute(new Thread(new InnerThread(redisConfig)));
                Long l = redisConfig.getSequence();
                System.out.println(l);
            }
        });
        thread.start();
        return "success!";
    }

    static class InnerThread implements Runnable {

        private RedisConfig redisConfig;

        public InnerThread(RedisConfig redisConfig) {
            this.redisConfig = redisConfig;
        }

        @Override
        public void run() {
            try {
                Long l = redisConfig.getSequence();
                System.out.println(l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class TimerThread implements Runnable {

        public TimerThread() {

        }

        @Override
        public void run() {
            try {
                Thread.sleep(10 * 1000L);
                go = false;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}