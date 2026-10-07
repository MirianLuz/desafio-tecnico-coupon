package desafio.tecnico.coupon_api.infrastructure.repository;

import desafio.tecnico.coupon_api.domain.entity.Coupon;
import desafio.tecnico.coupon_api.infrastructure.entity.JpaCouponEntity;
import desafio.tecnico.coupon_api.infrastructure.mapper.CouponMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CouponRepositoryImplTest {
    @Mock
    private JpaCouponRepository jpaRepository;

    @Mock
    private CouponMapper mapper;

    @InjectMocks
    private CouponRepositoyImpl repository;

    @Test
    void shouldSaveCoupon() {

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom",
                BigDecimal.TEN,
                LocalDateTime.now().plusDays(10),
                false
        );

        JpaCouponEntity entity = mock(JpaCouponEntity.class);

        when(mapper.toEntity(coupon))
                .thenReturn(entity);

        when(jpaRepository.save(entity))
                .thenReturn(entity);

        when(mapper.toDomain(entity))
                .thenReturn(coupon);

        Coupon result = repository.save(coupon);

        assertNotNull(result);
        assertEquals(
                coupon.getCode(),
                result.getCode()
        );

        verify(mapper)
                .toEntity(coupon);

        verify(jpaRepository)
                .save(entity);

        verify(mapper)
                .toDomain(entity);
    }

    @Test
    void shouldFindCouponById() {

        UUID id = UUID.randomUUID();

        JpaCouponEntity entity = mock(JpaCouponEntity.class);

        Coupon coupon = Coupon.create(
                "ABC123",
                "Cupom",
                BigDecimal.TEN,
                LocalDateTime.now().plusDays(10),
                false
        );

        when(jpaRepository.findById(id))
                .thenReturn(Optional.of(entity));

        when(mapper.toDomain(entity))
                .thenReturn(coupon);

        Optional<Coupon> result =
                repository.findById(id);

        assertTrue(result.isPresent());

        assertEquals(
                "ABC123",
                result.get().getCode()
        );

        verify(jpaRepository)
                .findById(id);

        verify(mapper)
                .toDomain(entity);
    }

    @Test
    void shouldReturnEmptyWhenCouponDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(jpaRepository.findById(id))
                .thenReturn(Optional.empty());

        Optional<Coupon> result =
                repository.findById(id);

        assertTrue(result.isEmpty());

        verify(jpaRepository)
                .findById(id);

        verify(mapper, never())
                .toDomain(any());
    }
}