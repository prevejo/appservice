package br.ucb.prevejo.core.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Service
public class PassiveCacheService<K, V> extends PassiveCacheProvider<K, V> {

    @Autowired
    private Collection<PassiveCacheContent> contents;

    @PostConstruct
    private void init() {
        contents.forEach(this::addCacheContent);
    }

}
