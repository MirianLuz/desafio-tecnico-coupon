package desafio.tecnico.coupon_api.domain.entity;

import desafio.tecnico.coupon_api.domain.enuns.CouponStatus;
import desafio.tecnico.coupon_api.domain.exceptions.BusinessException;
import desafio.tecnico.coupon_api.domain.exceptions.CouponAlreadyDeletedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Coupon {
    private final UUID id;
    private final String code;
    private final String description;
    private final BigDecimal discountValue;
    private final LocalDateTime expirationDate;
    private CouponStatus status;
    private final boolean published;
    private final boolean redeemed;
    private LocalDateTime deletedAt;

    private Coupon(
            UUID id,
            String code,
            String description,
            BigDecimal discountValue,
            LocalDateTime expirationDate,
            CouponStatus status,
            boolean published,
            boolean redeemed
    ) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.status = status;
        this.published = published;
        this.redeemed = redeemed;

        validate();
    }

    public static Coupon create(
            String code,
            String description,
            BigDecimal discountValue,
            LocalDateTime expirationDate,
            boolean published
    ) {

        String sanitizedCode = sanitizeCode(code);

        return new Coupon(
                UUID.randomUUID(),
                sanitizedCode,
                description,
                discountValue,
                expirationDate,
                CouponStatus.ACTIVE,
                published,
                false
        );
    }

    private void validate() {

        if (code == null || code.length() != 6) {
            throw new BusinessException(
                    "O código do cupom deve possuir exatamente 6 caracteres"
            );
        }

        if (description == null || description.isBlank()) {
            throw new BusinessException(
                    "A descrição do cupom é obrigatória"
            );
        }

        if (discountValue == null ||
                discountValue.compareTo(BigDecimal.valueOf(0.5)) < 0) {

            throw new BusinessException(
                    "O valor mínimo do desconto é 0,5"
            );
        }

        if (expirationDate == null) {
            throw new BusinessException(
                    "A data de expiração é obrigatória"
            );
        }

        if (!expirationDate.isAfter(LocalDateTime.now())) {
            throw new BusinessException(
                    "A data de expiração não pode estar no passado"
            );
        }
    }

    private static String sanitizeCode(String code) {

        if (code == null) {
            throw new BusinessException("O código é obrigatório");
        }

        String sanitized = code.replaceAll("[^a-zA-Z0-9]", "");

        if (sanitized.length() != 6) {
            throw new BusinessException(
                    "O código deve possuir 6 caracteres após a remoção dos caracteres especiais"
            );
        }

        return sanitized.toUpperCase();
    }

    public void delete() {

        if (status == CouponStatus.DELETED) {
            throw new CouponAlreadyDeletedException(
                    "O cupom já foi deletado"
            );
        }

        this.status = CouponStatus.DELETED;
        this.deletedAt = LocalDateTime.now();
    }

    public static Coupon restore(
            UUID id,
            String code,
            String description,
            BigDecimal discountValue,
            LocalDateTime expirationDate,
            CouponStatus status,
            boolean published,
            boolean redeemed,
            LocalDateTime deletedAt
    ) {

        Coupon coupon = new Coupon(
                id,
                code,
                description,
                discountValue,
                expirationDate,
                status,
                published,
                redeemed
        );

        coupon.deletedAt = deletedAt;

        return coupon;
    }

    public UUID getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public CouponStatus getStatus() {
        return status;
    }

    public boolean isPublished() {
        return published;
    }

    public boolean isRedeemed() {
        return redeemed;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
}