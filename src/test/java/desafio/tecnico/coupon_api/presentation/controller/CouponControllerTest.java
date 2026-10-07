package desafio.tecnico.coupon_api.presentation.controller;

import desafio.tecnico.coupon_api.application.dto.CouponRequest;
import desafio.tecnico.coupon_api.application.dto.CouponResponse;
import desafio.tecnico.coupon_api.application.service.CouponServiceImpl;
import desafio.tecnico.coupon_api.domain.enuns.CouponStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponServiceImpl couponService;

    @Test
    void shouldCreateCoupon() throws Exception {

        UUID id = UUID.randomUUID();

        LocalDateTime expirationDate =
                LocalDateTime.now().plusDays(10);

        CouponResponse response =
                new CouponResponse(
                        id,
                        "ABC123",
                        "Cupom",
                        BigDecimal.TEN,
                        expirationDate,
                        CouponStatus.ACTIVE,
                        true,
                        false
                );

        when(couponService.createCoupon(any(CouponRequest.class)))
                .thenReturn(response);

        String request = """
                {
                    "code": "ABC-123",
                    "description": "Cupom",
                    "discountValue": 10,
                    "expirationDate": "%s",
                    "published": true
                }
                """.formatted(expirationDate);

        mockMvc.perform(
                        post("/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.description").value("Cupom"))
                .andExpect(jsonPath("$.discountValue").value(10))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.published").value(true));

        verify(couponService)
                .createCoupon(any(CouponRequest.class));
    }

    @Test
    void shouldFindCouponById() throws Exception {

        UUID couponId = UUID.randomUUID();

        CouponResponse response = new CouponResponse(
                couponId,
                "ABC123",
                "Cupom de desconto",
                BigDecimal.valueOf(10.0),
                LocalDateTime.now().plusDays(10),
                CouponStatus.ACTIVE,
                true,
                false
        );

        when(couponService.getCoupon(couponId))
                .thenReturn(response);

        mockMvc.perform(
                        MockMvcRequestBuilders.get("/coupon/{id}", couponId)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(couponId.toString()))
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.description").value("Cupom de desconto"))
                .andExpect(jsonPath("$.discountValue").value(10.0))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.published").value(true))
                .andExpect(jsonPath("$.redeemed").value(false));

        verify(couponService).getCoupon(couponId);
    }

    @Test
    void shouldDeleteCoupon() throws Exception {

        UUID couponId = UUID.randomUUID();

        doNothing().when(couponService).deleteCoupon(couponId);

        mockMvc.perform(
                        delete("/coupon/{id}", couponId)
                )
                .andExpect(status().isNoContent());

        verify(couponService).deleteCoupon(couponId);
    }

    @Test
    void shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {

        String request = """
                {
                    "code": "",
                    "description": "",
                    "discountValue": 0.1,
                    "expirationDate": null,
                    "published": false
                }
                """;

        mockMvc.perform(
                        post("/coupon")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(request)
                )
                .andExpect(status().isBadRequest());

        verify(couponService, never())
                .createCoupon(any());
    }
}