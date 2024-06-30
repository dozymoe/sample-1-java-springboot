package moe.dozy.demo.sample1.services;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import moe.dozy.demo.sample1.Sample1Properties;

public class BaseService {

    protected AutowireCapableBeanFactory beanFactory;
    protected Sample1Properties properties;

    public BaseService(ApplicationContext ctx) {
        this.beanFactory = ctx.getAutowireCapableBeanFactory();
        this.properties = ctx.getBean(Sample1Properties.class);
    }
}
