package com.imooc.coupon.converter;

import com.imooc.coupon.CouponStatus;
import com.imooc.coupon.entity.Coupon;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CouponStatusConverter implements AttributeConverter<CouponStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    @Override
    public CouponStatus convertToEntityAttribute(Integer code) {
        return CouponStatus.of(code);
    }
}
