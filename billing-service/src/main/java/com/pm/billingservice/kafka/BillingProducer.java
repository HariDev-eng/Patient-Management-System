package com.pm.billingservice.kafka;

import events.BillingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillingProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void publishBillCreated(BillingEvent event) {
        publish(event);
    }

    public void publishBillPaid(BillingEvent event) {
        publish(event);
    }

    public void publishBillDeleted(BillingEvent event) {
        publish(event);
    }

    private void publish(BillingEvent event) {

        kafkaTemplate.send(
                "billing-events",
                event.getBillId(),
                event.toByteArray()
        );
    }
}