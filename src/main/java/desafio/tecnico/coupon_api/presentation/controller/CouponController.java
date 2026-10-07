package desafio.tecnico.coupon_api.presentation.controller;

import desafio.tecnico.coupon_api.application.dto.CouponRequest;
import desafio.tecnico.coupon_api.application.dto.CouponResponse;
import desafio.tecnico.coupon_api.application.usecase.CouponUseCases;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private static final Logger log =
            LoggerFactory.getLogger(CouponController.class);

    private final CouponUseCases couponUseCases;

    public CouponController(CouponUseCases couponUseCases) {
        this.couponUseCases = couponUseCases;
    }

    @PostMapping
    public ResponseEntity<CouponResponse> create(@Valid @RequestBody CouponRequest request) {

        log.info("Recebida solicitação para criação de cupom. code={}",
                request.code());

        CouponResponse response = couponUseCases.createCoupon(request);

        log.info("Cupom criado com sucesso. id={}, code={}",
                response.id(),
                response.code());

        return ResponseEntity
                .created(URI.create("/coupon/" + response.id()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable UUID id) {
        log.info("Consultando cupom. id={}", id);
        return ResponseEntity.ok(
                couponUseCases.getCoupon(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable UUID id) {
        log.info("Solicitação de exclusão de cupom. id={}", id);
        couponUseCases.deleteCoupon(id);
        log.info("Cupom excluído logicamente com sucesso. id={}", id);
        return ResponseEntity.noContent().build();
    }
}