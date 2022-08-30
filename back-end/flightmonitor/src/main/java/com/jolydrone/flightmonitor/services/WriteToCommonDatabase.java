package com.jolydrone.flightmonitor.services;

import com.jolydrone.flightmonitor.configurations.Config;
import com.jolydrone.flightmonitor.entity.Delivery;
import com.jolydrone.flightmonitor.repositories.DeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

@Component
@Scope
public class WriteToCommonDatabase {

    String DB_WRITE_LOCK_NAME = "dbWriteLock";
    Duration REDIS_WRITE_LOCK_TIMEOUT = Duration.ofMinutes(30);

    Logger logger = LoggerFactory.getLogger(WriteToCommonDatabase.class.getName());
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired()
    @Qualifier("lockTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    @Qualifier("dataTemplate")
    private RedisTemplate<String, Object> dataRedisTemplate;

    @Autowired
    public WriteToCommonDatabase() {
    }

    public void saveDeliveryDone(Delivery delivery) {


        try {

            LockNameAndState lockNameAndState = getLockOnCommonDb();
            writeDeliveryInCommonDb(delivery);
            releaseLockOnCommonDb(lockNameAndState);
        } catch (Exception e) {
            logger.warn(e.toString());
        }

    }

    private void writeDeliveryInCommonDb(Delivery delivery) {
        var optionalDeliveryFounded = deliveryRepository.findByTrackingNumber(delivery.getTrackingNumber());

        if (optionalDeliveryFounded.isPresent()) {
            var deliveryFounded = optionalDeliveryFounded.get();
            deliveryFounded.setDeliveryStatus(delivery.getDeliveryStatus());
            deliveryFounded.setDrone(delivery.getDrone());
            deliveryFounded.setFlightPlan(delivery.getFlightPlan());
            deliveryFounded.setTrackingNumber(delivery.getTrackingNumber());
            deliveryFounded.setPaquet(delivery.getPaquet());
            deliveryRepository.save(delivery);
        }
    }

    @Scheduled(cron = "*/60 * * * * *")
    private void saveData() {
        // The code below acquires the dbWriteLock.
        logger.info("Scheduller called");

        try {

            LockNameAndState lockNameAndState = getLockOnCommonDb();

            if (Boolean.TRUE.equals(lockNameAndState.lockAcquired)) {
                var progresses = dataRedisTemplate.opsForList().range(Config.PROGRESS_DB_KEY, 0, -1);
                var progresses_size = Objects.requireNonNull(progresses).size();
                logger.info(String.format("Sauvegarde de %s donnée(s) de progressions dans la base de données commune.", progresses_size));
                for (int i = 0; i < progresses_size; i++) {
                    var progress = (Delivery) progresses.get(i);

                    writeDeliveryInCommonDb(progress);
                }
                dataRedisTemplate.opsForList().getOperations().delete(Config.PROGRESS_DB_KEY);
            }

            releaseLockOnCommonDb(lockNameAndState);
        } catch (Exception e) {
            logger.warn(e.toString());
        }
    }

    private void releaseLockOnCommonDb(LockNameAndState lockNameAndState) {
        if (lockNameAndState.lockName.equals(redisTemplate.opsForValue().get(DB_WRITE_LOCK_NAME))) {
            redisTemplate.delete(DB_WRITE_LOCK_NAME);
        }
    }

    private LockNameAndState getLockOnCommonDb() {
        String lockName = UUID.randomUUID().toString();
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(DB_WRITE_LOCK_NAME, lockName,
                REDIS_WRITE_LOCK_TIMEOUT);
        return new LockNameAndState(lockName, lockAcquired);
    }

    public record LockNameAndState(String lockName, Boolean lockAcquired) {

    }
}
