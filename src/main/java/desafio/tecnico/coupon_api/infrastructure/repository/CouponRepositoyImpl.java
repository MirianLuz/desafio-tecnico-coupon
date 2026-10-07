package desafio.tecnico.coupon_api.infrastructure.repository;

import desafio.tecnico.coupon_api.domain.entity.Coupon;
import desafio.tecnico.coupon_api.domain.repository.CouponRepository;
import desafio.tecnico.coupon_api.infrastructure.mapper.CouponMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CouponRepositoyImpl implements CouponRepository {

    private final JpaCouponRepository jpaCouponRepository;

    private final CouponMapper mapper;

    public CouponRepositoyImpl(
            JpaCouponRepository jpaRepository,
            CouponMapper mapper
    ) {
        this.jpaCouponRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Coupon save(Coupon coupon) {

        var entity = mapper.toEntity(coupon);

        var saved = jpaCouponRepository.save(entity);

        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Coupon> findById(UUID id) {

        return jpaCouponRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaCouponRepository.existsByCode(code);
    }
}