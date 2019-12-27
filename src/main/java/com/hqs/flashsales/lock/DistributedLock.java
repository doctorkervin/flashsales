package com.hqs.flashsales.lock;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @title 分布式锁组件
 * @description 分布式锁组件
 * @author kervin
 * @updateTime 2019/12/27 0027 16:16 No such property: code for class: Script1
 */
@Slf4j
@Component
public class DistributedLock {


    //注意RedisTemplate用的String,String，后续所有用到的key和value都是String的
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    RedisScript<Boolean> lockScript;

    @Autowired
    RedisScript<Long> unlockScript;

    /**
     * @title distributedLock
     * @description 分布式加锁
     * @author kervin
     * @updateTime 2019/12/27 0027 15:53 No such property: code for class: Script1
     * @throws
     */
    public Boolean distributedLock(String key, String uuid, String secondsToLock) {
        Boolean locked = false;
        try {
            String millSeconds = String.valueOf(Integer.parseInt(secondsToLock) * 1000);
            locked =redisTemplate.execute(lockScript, Collections.singletonList(key), uuid, millSeconds);
            log.info("distributedLock.key{}: - uuid:{}: - timeToLock:{} - locked:{} - millSeconds:{}",
                    key, uuid, secondsToLock, locked, millSeconds);
        } catch (Exception e) {
            log.error("error", e);
        }
        return locked;
    }

    /**
     * @title distributedUnlock
     * @description 分布式解锁
     * @author kervin
     * @updateTime 2019/12/27 0027 15:53 No such property: code for class: Script1
     * @throws
     */
    public void distributedUnlock(String key, String uuid) {
        Long unlocked = redisTemplate.execute(unlockScript, Collections.singletonList(key),
                uuid);
        log.info("distributedLock.key{}: - uuid:{}: - unlocked:{}", key, uuid, unlocked);

    }

}
