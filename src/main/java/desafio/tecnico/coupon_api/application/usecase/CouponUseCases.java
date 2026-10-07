package desafio.tecnico.coupon_api.application.usecase;

import desafio.tecnico.coupon_api.application.dto.CouponRequest;
import desafio.tecnico.coupon_api.application.dto.CouponResponse;

import java.util.UUID;

public interface CouponUseCases {

    CouponResponse createCoupon(CouponRequest request);

    CouponResponse getCoupon(UUID id);

    void deleteCoupon(UUID id);
}