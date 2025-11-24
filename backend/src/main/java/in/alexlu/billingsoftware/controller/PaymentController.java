package in.alexlu.billingsoftware.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import in.alexlu.billingsoftware.io.OrderResponse;
import in.alexlu.billingsoftware.io.PaymentRequest;
import in.alexlu.billingsoftware.io.PaymentVerificationRequest;
import in.alexlu.billingsoftware.io.StripeOrderResponse;
import in.alexlu.billingsoftware.service.OrderService;
import in.alexlu.billingsoftware.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.stripe.model.checkout.Session;
import com.stripe.exception.SignatureVerificationException;
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final StripeService stripeService;
    private final OrderService orderService;

    @PostMapping("/create-order")
    @ResponseStatus(HttpStatus.CREATED)
    public StripeOrderResponse createStripeOrder(@RequestBody PaymentRequest request) throws Exception {
        // 调用 StripeService 创建订单
        return stripeService.createOrder(request.getAmount(), request.getCurrency(),request.getOrderId());
    }

    @PostMapping("/verify")
    public OrderResponse verifyPayment(@RequestBody PaymentVerificationRequest request) {
        return orderService.verifyPayment(request);
    }
//    @PostMapping("/webhook")
//    public ResponseEntity<String> handleStripeWebhook(
//            @RequestHeader("Stripe-Signature") String signature,
//            @RequestBody String payload) {
//
//        Stripe.apiKey = "sk_test_xxxx";
//
//        Event event;
//        try {
//            event = Webhook.constructEvent(payload, signature, endpointSecret);
//        } catch (SignatureVerificationException e) {
//            return ResponseEntity.badRequest().body("Invalid Stripe Signature");
//        }
//
//        if ("checkout.session.completed".equals(event.getType())) {
//
//            Session session = (Session) event.getDataObjectDeserializer()
//                    .getObject()
//                    .orElse(null);
//
//            if (session != null) {
//                String orderId = session.getClientReferenceId();  // 从创建 session 时带的订单号取回来
//                orderService.markOrderAsPaid(orderId);            // 更新订单状态
//            }
//        }
//
//        return ResponseEntity.ok("success");
//    }
}
