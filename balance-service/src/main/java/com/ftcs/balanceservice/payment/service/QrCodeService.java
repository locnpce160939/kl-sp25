package com.ftcs.balanceservice.payment.service;

import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.balanceservice.payment.repository.PaymentRepository;
import com.ftcs.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QrCodeService {

    @Value("${vietqr.api.url}")
    private String vietQrApiUrl;

    @Value("${vietqr.bank.account}")
    private String bankAccount;

    @Value("${vietqr.bank.holder}")
    private String accountHolder;

    @Value("${vietqr.bank.id}")
    private int bankId;

    private final PaymentRepository paymentRepository;

    public String generateQrCode(Payment payment) {

        try {
            HttpPost request = createQrCodeRequest(payment);
            HttpResponse response = HttpClients.createDefault().execute(request);
            String qrDataUrl = extractQrDataUrl(response);

            payment.setQrData(qrDataUrl);
            paymentRepository.save(payment);

            return qrDataUrl;
        } catch (Exception e) {
            log.error("QR code generation failed for payment ID: {}", payment.getPaymentId(), e);
            throw new BadRequestException("QR code generation failed: " + e.getMessage());
        }
    }

    private HttpPost createQrCodeRequest(Payment payment) throws Exception {
        HttpPost request = new HttpPost(vietQrApiUrl);
        request.setHeader("Content-Type", "application/json");
        JSONObject payload = new JSONObject()
                .put("accountNo", bankAccount)
                .put("accountName", accountHolder)
                .put("acqId", bankId)
                .put("amount", payment.getAmount())
                .put("addInfo", "Pay for TripBookingId " + payment.getBookingId())
                .put("format", "text")
                .put("template", "compact");
        System.out.println(payload);
        request.setEntity(new StringEntity(payload.toString()));
        return request;
    }

    private String extractQrDataUrl(HttpResponse response) throws Exception {
        String jsonResponse = EntityUtils.toString(response.getEntity());
        JSONObject responseObj = new JSONObject(jsonResponse);
        if (!responseObj.has("data")) {
            log.error("Missing 'data' field in response: {}", jsonResponse);
            throw new BadRequestException("Invalid QR API response");
        }

        return responseObj.getJSONObject("data").optString("qrDataURL", "");
    }
}
