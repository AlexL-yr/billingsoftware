package in.alexlu.billingsoftware.service.impl;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import in.alexlu.billingsoftware.io.OrderResponse;
import in.alexlu.billingsoftware.io.StripeOrderResponse;
import in.alexlu.billingsoftware.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Override
    public StripeOrderResponse createOrder(Double amount, String currency,String orderId) throws Exception {
        Stripe.apiKey = stripeSecretKey;

        long amountInCents = (long) (amount * 100);

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setClientReferenceId(orderId)
                .setSuccessUrl("http://localhost:5173/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:5173/cancel")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount(amountInCents)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Payment")
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();

        Session session = Session.create(params);

        return StripeOrderResponse.builder()
                .id(session.getId())
                .entity("checkout.session")
                .amount((int) amountInCents)
                .currency(currency)
                .status(session.getStatus())
                .create_at(new Date(session.getCreated() * 1000))
                .receipt(session.getClientReferenceId())
                .build();
    }

}
