package br.ucb.prevejo.core;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

@Component
public class ContextProvider implements ApplicationContextAware {

    private static ApplicationContext CONTEXT;
    private static Repositories REPS;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CONTEXT = applicationContext;
        REPS = new Repositories(applicationContext);
    }

    /**
     * Get a Spring bean by type.
     **/
    public static <T> T getBean(Class<T> beanClass) {
        return CONTEXT.getBean(beanClass);
    }

    /**
     * Geo all String beans of a type
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> beanClass) {
        return CONTEXT.getBeansOfType(beanClass);
    }

    /**
     * Get a Spring bean by name.
     **/
    public static Object getBean(String beanName) {
        return CONTEXT.getBean(beanName);
    }

    public static Resource getResource(String resource) {
        return CONTEXT.getResource(resource);
    }

    @SuppressWarnings("unchecked")
    public static <T> JpaRepository<T, ?> getJpaRepository(Class<T> classe) {
        return (JpaRepository<T, ?>) REPS.getRepositoryFor(classe).get();
    }

}