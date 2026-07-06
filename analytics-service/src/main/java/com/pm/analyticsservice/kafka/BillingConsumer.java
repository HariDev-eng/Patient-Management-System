package com.pm.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.analyticsservice.service.AnalyticsService;
import events.BillingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillingConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "billing-events",
            groupId = "analytics-service"
    )
    public void consume(byte[] payload)
            throws InvalidProtocolBufferException {

        BillingEvent event = BillingEvent.parseFrom(payload);

        switch (event.getEventType()) {

            case "BILL_CREATED" ->
                    analyticsService.saveBill(event);

            case "BILL_PAID" ->
                    analyticsService.markBillPaid(event);

            case "BILL_CANCELLED" ->
                    analyticsService.cancelBill(event);

            default ->
                    throw new IllegalArgumentException(
                            "Unknown billing event: "
                                    + event.getEventType());
        }
    }
}