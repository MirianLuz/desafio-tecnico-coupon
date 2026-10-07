package desafio.tecnico.coupon_api.application.service;


import desafio.tecnico.coupon_api.application.dto.CouponRequest;
import desafio.tecnico.coupon_api.application.dto.CouponResponse;
import desafio.tecnico.coupon_api.application.usecase.CouponUseCases;
import desafio.tecnico.coupon_api.domain.entity.Coupon;
import desafio.tecnico.coupon_api.domain.exceptions.CouponAlreadyExistsException;
import desafio.tecnico.coupon_api.domain.exceptions.CouponNotFoundException;
import desafio.tecnico.coupon_api.domain.repository.CouponRepository;
import desafio.tecnico.coupon_api.infrastructure.mapper.CouponMapper;
import desafio.tecnico.coupon_api.presentation.controller.CouponController;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CouponServiceImpl implements CouponUseCases {

    private static final Logger log =
            LoggerFactory.getLogger(CouponController.class);

    private final CouponRepository couponRepository;

    private final CouponMapper couponMapper;

    public CouponServiceImpl(CouponRepository couponRepository, CouponMapper couponMapper) {
        this.couponRepository = couponRepository;
        this.couponMapper = couponMapper;
    }

    @Override
    @Transactional
    public CouponResponse createCoupon(CouponRequest request) {
        log.info("Iniciando criação do cupom. code={}", request.code());

        Coupon coupon = Coupon.create(
                request.code(),
                request.description(),
                request.discountValue(),
                request.expirationDate(),
                Boolean.TRUE.equals(request.published())
        );

        log.debug("Cupom criado no domínio. id={}, code={}, published={}",
                coupon.getId(),
                coupon.getCode(),
                coupon.isPublished());

        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new CouponAlreadyExistsException(coupon.getCode());
        }

        Coupon savedCoupon = couponRepository.save(coupon);

        log.info("Cupom persistido com sucesso. id={}, code={}",
                savedCoupon.getId(),
                savedCoupon.getCode());

        return toResponse(savedCoupon);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCoupon(UUID id) {

        log.info("Buscando cupom. id={}", id);

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() ->
                        new CouponNotFoundException(
                                "Cupom não encontrado: " + id
                        )
                );

        log.debug("Cupom encontrado. id={}, status={}",
                coupon.getId(),
                coupon.getStatus());

        return toResponse(coupon);
    }

    @Override
    @Transactional
    public void deleteCoupon(UUID id) {
        log.info("Iniciando exclusão lógica do cupom. id={}", id);

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentativa de excluir cupom inexistente. id={}", id);

                    return new CouponNotFoundException(
                            "Cupom não encontrado: " + id
                    );
                });

        coupon.delete();

        log.info("Exclusão lógica concluída. id={}, status={}",
                coupon.getId(),
                coupon.getStatus());

        couponRepository.save(coupon);
    }

    private CouponResponse toResponse(Coupon coupon) {

        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.getStatus(),
                coupon.isPublished(),
                coupon.isRedeemed()
        );
    }
}