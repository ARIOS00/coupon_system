package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.CouponStatus;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {
    private final StringRedisTemplate redisTemplate;
    @Autowired
    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @Override
    public List<Coupon> getCacheCoupons(Long userId, Integer status) {
        log.info("Get Coupons From Cache: {}, {}", userId, status);
        String redisKey = statusToRedisKey(status, userId);
        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey).stream().map(o -> Objects.toString(o)).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(couponStrs)) {
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        return couponStrs.stream().map(s -> JSON.parseObject(s, Coupon.class)).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("all")
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty List to Cache. UserId: {}, Status: {}", userId, JSON.toJSONString(status));
        Map<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));
        // Redis Session Callback, put command into Redis pipeline (async)
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s -> {
                    String redisKey = statusToRedisKey(s, userId);
                    redisOperations.opsForHash().putAll(redisKey, invalidCouponMap);
                });
                return null;
            }
        };
        log.info("Redis Pipeline: {}", JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
    }

    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        return null;
    }

    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        return null;
    }

    private String statusToRedisKey(Integer status, Long userId) {
        String redisKey = null;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus) {
            case USABLE:
                redisKey = String.format("%s%s", Constant.RedisPredix.USER_COUPON_USABLE, userId);
                break;
            case USED:
                redisKey = String.format("%s%s", Constant.RedisPredix.USER_COUPON_USED, userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s", Constant.RedisPredix.USER_COUPON_EXPIRED, userId);
                break;
        }
        return redisKey;
    }
}
