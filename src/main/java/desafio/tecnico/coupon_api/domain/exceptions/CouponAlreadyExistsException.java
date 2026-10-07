package desafio.tecnico.coupon_api.domain.exceptions;

public class CouponAlreadyExistsException extends BusinessException {

    public CouponAlreadyExistsException(String code) {
        super(String.format("Já existe um cupom cadastrado com este código.", code));
    }
}