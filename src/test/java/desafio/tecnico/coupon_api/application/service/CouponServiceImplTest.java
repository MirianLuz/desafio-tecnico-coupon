package desafio.tecnico.coupon_api.application.service;

import desafio.tecnico.coupon_api.application.dto.CouponRequest;
import desafio.tecnico.coupon_api.application.dto.CouponResponse;
import desafio.tecnico.coupon_api.domain.entity.Coupon;
import desafio.tecnico.coupon_api.domain.enuns.CouponStatus;
import desafio.tecnico.coupon_api.domain.exceptions.CouponAlreadyDeletedException;
import desafio.tecnico.coupon_api.domain.exceptions.CouponNotFoundException;
import desafio.tecnico.coupon_api.domain.repository.CouponRepository;
import desafio.tecnico.coupon_api.infrastructure.mapper.CouponMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponMapper couponMapper;

    private CouponServiceImpl couponService;

    private LocalDateTime futureDate;

    @BeforeEach
    void setUp() {

        couponService = new CouponServiceImpl(
                couponRepository, couponMapper
        );

        futureDate = LocalDateTime.now().plusDays(10);
    }

    @Test
    void shouldCreateCoupon() {

        CouponRequest request =
                new CouponRequest(
                        "ABC-123",
                        "Cupom de desconto",
                        BigDecimal.TEN,
                        futureDate,
                        false
                );

        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        CouponResponse response =
                couponService.createCoupon(request);

        assertNotNull(response);
        assertEquals("ABC123", response.code());
        assertEquals(
                "Cupom de desconto",
                response.description()
        );
        assertEquals(
                BigDecimal.TEN,
                response.discountValue()
        );
        assertFalse(response.published());
        assertEquals(
                CouponStatus.ACTIVE,
                response.status()
        );

        verify(couponRepository)
                .save(any(Coupon.class));
    }

    @Test
    void shouldCreatePublishedCoupon() {

        CouponRequest request =
                new CouponRequest(
                        "ABC123",
                        "Cupom",
                        BigDecimal.TEN,
                        futureDate,
                        true
                );

        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        CouponResponse response =
                couponService.createCoupon(request);

        assertTrue(response.published());
    }

    @Test
    void shouldCreateUnpublishedCouponWhenPublishedIsNull() {

        CouponRequest request =
                new CouponRequest(
                        "ABC123",
                        "Cupom",
                        BigDecimal.TEN,
                        futureDate,
                        null
                );

        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        CouponResponse response =
                couponService.createCoupon(request);

        assertFalse(response.published());
    }

    @Test
    void shouldFindCouponById() {

        UUID id = UUID.randomUUID();

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom",
                BigDecimal.TEN,
                futureDate,
                false
        );

        when(couponRepository.findById(id))
                .thenReturn(Optional.of(coupon));

        CouponResponse response =
                couponService.getCoupon(id);

        assertNotNull(response);
        assertEquals("ABC123", response.code());

        verify(couponRepository)
                .findById(id);
    }

    @Test
    void shouldThrowExceptionWhenCouponDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(couponRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                CouponNotFoundException.class,
                () -> couponService.getCoupon(id)
        );

        verify(couponRepository)
                .findById(id);
    }

    @Test
    void shouldDeleteCoupon() {

        UUID id = UUID.randomUUID();

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom",
                BigDecimal.TEN,
                futureDate,
                false
        );

        when(couponRepository.findById(id))
                .thenReturn(Optional.of(coupon));

        when(couponRepository.save(any(Coupon.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        couponService.deleteCoupon(id);

        ArgumentCaptor<Coupon> captor =
                ArgumentCaptor.forClass(Coupon.class);

        verify(couponRepository)
                .save(captor.capture());

        Coupon deletedCoupon = captor.getValue();

        assertEquals(
                CouponStatus.DELETED,
                deletedCoupon.getStatus()
        );

        assertNotNull(
                deletedCoupon.getDeletedAt()
        );
    }

    @Test
    void shouldNotDeleteNonExistingCoupon() {

        UUID id = UUID.randomUUID();

        when(couponRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                CouponNotFoundException.class,
                () -> couponService.deleteCoupon(id)
        );

        verify(couponRepository, never())
                .save(any());
    }

    @Test
    void shouldNotDeleteCouponAlreadyDeleted() {

        UUID id = UUID.randomUUID();

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom",
                BigDecimal.TEN,
                futureDate,
                false
        );

        coupon.delete();

        when(couponRepository.findById(id))
                .thenReturn(Optional.of(coupon));

        assertThrows(
                CouponAlreadyDeletedException.class,
                () -> couponService.deleteCoupon(id)
        );

        verify(couponRepository, never())
                .save(any());
    }
}