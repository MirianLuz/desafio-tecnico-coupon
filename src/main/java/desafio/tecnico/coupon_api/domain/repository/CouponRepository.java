package desafio.tecnico.coupon_api.domain.repository;

import desafio.tecnico.coupon_api.domain.entity.Coupon;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(UUID id);

    boolean existsByCode(String code);
}