package com.wessup.daily.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;


@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    private static int THREAD_POOL_SIZE = 4;
    private static int TASK_QUEUE_CAPACITY = 16;
    private Logger logger = LoggerFactory.getLogger(AsyncConfig.class);

    @Bean("threadExecutor")
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(THREAD_POOL_SIZE);
        executor.setMaxPoolSize(THREAD_POOL_SIZE);
        executor.setQueueCapacity(TASK_QUEUE_CAPACITY);
        executor.setThreadNamePrefix("AsyncExecutor-");
//        executor.setBeanName("asyncExecutor");
        executor.initialize();

        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }

    public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
            String msg = String.format("Exception: %s\nFrom Method: %s\n", throwable.getMessage(), method.getName());
            logger.error(msg);
            String parameters = "Parameters : \n";
            for (Object temp: objects) {
                parameters += temp + "\n";
            }
            logger.error(parameters);
        }
    }

}
