package in.alexlu.billingsoftware.service;

import in.alexlu.billingsoftware.io.StripeOrderResponse;

public interface StripeService {

    StripeOrderResponse createOrder(Double amount, String currency,String orderId) throws Exception;
}
