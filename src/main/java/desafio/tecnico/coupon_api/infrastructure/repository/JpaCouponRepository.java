package desafio.tecnico.coupon_api.infrastructure.repository;

import desafio.tecnico.coupon_api.infrastructure.entity.JpaCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaCouponRepository extends JpaRepository<JpaCouponEntity, UUID> {

    boolean existsByCode(String code);

}