package desafio.tecnico.coupon_api.infrastructure.mapper;

import desafio.tecnico.coupon_api.domain.entity.Coupon;
import desafio.tecnico.coupon_api.infrastructure.entity.JpaCouponEntity;
import org.springframework.stereotype.Component;

@Component
public class CouponMapper {

    public JpaCouponEntity toEntity(Coupon coupon) {

        return new JpaCouponEntity(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.getStatus(),
                coupon.isPublished(),
                coupon.isRedeemed(),
                coupon.getDeletedAt()
        );
    }

    public Coupon toDomain(JpaCouponEntity entity) {

        return Coupon.restore(
                entity.getId(),
                entity.getCode(),
                entity.getDescription(),
                entity.getDiscountValue(),
                entity.getExpirationDate(),
                entity.getStatus(),
                entity.isPublished(),
                entity.isRedeemed(),
                entity.getDeletedAt()
        );
    }
}