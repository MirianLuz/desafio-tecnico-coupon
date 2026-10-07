package desafio.tecnico.coupon_api.infrastructure.mapper;

import desafio.tecnico.coupon_api.domain.entity.Coupon;
import desafio.tecnico.coupon_api.domain.enuns.CouponStatus;
import desafio.tecnico.coupon_api.infrastructure.entity.JpaCouponEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CouponMapperTest {

    private final CouponMapper mapper =
            new CouponMapper();

    @Test
    void shouldConvertDomainToEntity() {

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom",
                BigDecimal.TEN,
                LocalDateTime.now().plusDays(10),
                true
        );

        JpaCouponEntity entity =
                mapper.toEntity(coupon);

        assertEquals(
                coupon.getId(),
                entity.getId()
        );

        assertEquals(
                coupon.getCode(),
                entity.getCode()
        );

        assertEquals(
                coupon.getDescription(),
                entity.getDescription()
        );

        assertEquals(
                coupon.getDiscountValue(),
                entity.getDiscountValue()
        );

        assertEquals(
                coupon.getExpirationDate(),
                entity.getExpirationDate()
        );

        assertEquals(
                coupon.getStatus(),
                entity.getStatus()
        );

        assertEquals(
                coupon.isPublished(),
                entity.isPublished()
        );

        assertEquals(
                coupon.isRedeemed(),
                entity.isRedeemed()
        );

        assertEquals(
                coupon.getDeletedAt(),
                entity.getDeletedAt()
        );
    }

    @Test
    void shouldConvertDeletedDomainToEntity() {

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom",
                BigDecimal.TEN,
                LocalDateTime.now().plusDays(10),
                false
        );

        coupon.delete();

        JpaCouponEntity entity =
                mapper.toEntity(coupon);

        assertEquals(
                coupon.getStatus(),
                entity.getStatus()
        );

        assertEquals(
                coupon.getDeletedAt(),
                entity.getDeletedAt()
        );
    }

    @Test
    void shouldConvertEntityToDomain() {

        UUID id = UUID.randomUUID();

        LocalDateTime expirationDate =
                LocalDateTime.now().plusDays(10);

        JpaCouponEntity entity = new JpaCouponEntity(
                id,
                "ABC123",
                "Cupom",
                BigDecimal.TEN,
                expirationDate,
                CouponStatus.ACTIVE,
                true,
                false,
                null
        );

        Coupon coupon =
                mapper.toDomain(entity);

        assertEquals(id, coupon.getId());
        assertEquals("ABC123", coupon.getCode());
        assertEquals("Cupom", coupon.getDescription());
        assertEquals(BigDecimal.TEN, coupon.getDiscountValue());
        assertEquals(expirationDate, coupon.getExpirationDate());
        assertEquals(CouponStatus.ACTIVE, coupon.getStatus());
        assertTrue(coupon.isPublished());
        assertFalse(coupon.isRedeemed());
        assertNull(coupon.getDeletedAt());
    }
}