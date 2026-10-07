package desafio.tecnico.coupon_api.domain.entity;

import desafio.tecnico.coupon_api.domain.enuns.CouponStatus;
import desafio.tecnico.coupon_api.domain.exceptions.BusinessException;
import desafio.tecnico.coupon_api.domain.exceptions.CouponAlreadyDeletedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CouponTest {

    private final LocalDateTime futureDate =
            LocalDateTime.now().plusDays(10);

    @Test
    void shouldCreateCouponSuccessfully() {

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom de desconto",
                BigDecimal.valueOf(10),
                futureDate,
                false
        );

        assertNotNull(coupon.getId());
        assertEquals("ABC123", coupon.getCode());
        assertEquals(
                "Cupom de desconto",
                coupon.getDescription()
        );
        assertEquals(
                BigDecimal.valueOf(10),
                coupon.getDiscountValue()
        );
        assertEquals(
                futureDate,
                coupon.getExpirationDate()
        );
        assertEquals(
                CouponStatus.ACTIVE,
                coupon.getStatus()
        );
        assertFalse(coupon.isPublished());
        assertFalse(coupon.isRedeemed());
        assertNull(coupon.getDeletedAt());
    }

    @Test
    void shouldCreatePublishedCoupon() {

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom publicado",
                BigDecimal.valueOf(10),
                futureDate,
                true
        );

        assertTrue(coupon.isPublished());
    }

    @Test
    void shouldRemoveSpecialCharactersFromCode() {

        Coupon coupon = Coupon.create(
                "ABC-123",
                "Cupom",
                BigDecimal.valueOf(10),
                futureDate,
                false
        );

        assertEquals("ABC123", coupon.getCode());
    }

    @Test
    void shouldConvertCodeToUpperCase() {

        Coupon coupon = Coupon.create(
                "abc123",
                "Cupom",
                BigDecimal.valueOf(10),
                futureDate,
                false
        );

        assertEquals("ABC123", coupon.getCode());
    }

    @Test
    void shouldAcceptSpecialCharactersWhenResultHasSixCharacters() {

        Coupon coupon = Coupon.create(
                "A-B@C#1$2%3",
                "Cupom",
                BigDecimal.valueOf(10),
                futureDate,
                false
        );

        assertEquals("ABC123", coupon.getCode());
    }

    @Test
    void shouldNotCreateCouponWhenCodeIsNull() {

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> Coupon.create(
                        null,
                        "Cupom",
                        BigDecimal.TEN,
                        futureDate,
                        false
                )
        );

        assertEquals(
                "O código é obrigatório",
                exception.getMessage()
        );
    }

    @Test
    void shouldNotCreateCouponWhenCodeHasLessThanSixCharacters() {

        assertThrows(
                BusinessException.class,
                () -> Coupon.create(
                        "ABC12",
                        "Cupom",
                        BigDecimal.TEN,
                        futureDate,
                        false
                )
        );
    }

    @Test
    void shouldNotCreateCouponWhenCodeHasMoreThanSixCharacters() {

        assertThrows(
                BusinessException.class,
                () -> Coupon.create(
                        "ABC1234",
                        "Cupom",
                        BigDecimal.TEN,
                        futureDate,
                        false
                )
        );
    }

    @Test
    void shouldNotCreateCouponWhenCodeContainsOnlySpecialCharacters() {

        assertThrows(
                BusinessException.class,
                () -> Coupon.create(
                        "@#$%-!",
                        "Cupom",
                        BigDecimal.TEN,
                        futureDate,
                        false
                )
        );
    }

    @Test
    void shouldNotCreateCouponWhenDescriptionIsNull() {

        assertThrows(
                BusinessException.class,
                () -> Coupon.create(
                        "ABC123",
                        null,
                        BigDecimal.TEN,
                        futureDate,
                        false
                )
        );
    }

    @Test
    void shouldNotCreateCouponWhenDescriptionIsBlank() {

        assertThrows(
                BusinessException.class,
                () -> Coupon.create(
                        "ABC123",
                        "   ",
                        BigDecimal.TEN,
                        futureDate,
                        false
                )
        );
    }

    @Test
    void shouldNotCreateCouponWhenDiscountIsNull() {

        assertThrows(
                BusinessException.class,
                () -> Coupon.create(
                        "ABC123",
                        "Cupom",
                        null,
                        futureDate,
                        false
                )
        );
    }

    @Test
    void shouldNotCreateCouponWhenDiscountIsBelowMinimum() {

        assertThrows(
                BusinessException.class,
                () -> Coupon.create(
                        "ABC123",
                        "Cupom",
                        BigDecimal.valueOf(0.49),
                        futureDate,
                        false
                )
        );
    }

    @Test
    void shouldAcceptMinimumDiscount() {

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom",
                BigDecimal.valueOf(0.5),
                futureDate,
                false
        );

        assertEquals(
                BigDecimal.valueOf(0.5),
                coupon.getDiscountValue()
        );
    }

    @Test
    void shouldAcceptDiscountGreaterThanMinimum() {

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom",
                BigDecimal.valueOf(10000),
                futureDate,
                false
        );

        assertEquals(
                BigDecimal.valueOf(10000),
                coupon.getDiscountValue()
        );
    }

    @Test
    void shouldNotCreateCouponWhenExpirationDateIsNull() {

        assertThrows(
                BusinessException.class,
                () -> Coupon.create(
                        "ABC123",
                        "Cupom",
                        BigDecimal.TEN,
                        null,
                        false
                )
        );
    }

    @Test
    void shouldNotCreateCouponWhenExpirationDateIsInPast() {

        LocalDateTime pastDate =
                LocalDateTime.now().minusDays(1);

        assertThrows(
                BusinessException.class,
                () -> Coupon.create(
                        "ABC123",
                        "Cupom",
                        BigDecimal.TEN,
                        pastDate,
                        false
                )
        );
    }

    @Test
    void shouldDeleteCouponUsingSoftDelete() {

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom",
                BigDecimal.TEN,
                futureDate,
                false
        );

        coupon.delete();

        assertEquals(
                CouponStatus.DELETED,
                coupon.getStatus()
        );

        assertNotNull(coupon.getDeletedAt());

        assertEquals(
                "ABC123",
                coupon.getCode()
        );

        assertEquals(
                "Cupom",
                coupon.getDescription()
        );
    }

    @Test
    void shouldNotDeleteCouponTwice() {

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom",
                BigDecimal.TEN,
                futureDate,
                false
        );

        coupon.delete();

        assertThrows(
                CouponAlreadyDeletedException.class,
                coupon::delete
        );
    }
}