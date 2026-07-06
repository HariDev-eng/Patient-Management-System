package com.pm.analyticsservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.pm.analyticsservice.service.AnalyticsService;
import events.VitalsRecordedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VitalsConsumer {

    private final AnalyticsService analyticsService;

    @KafkaListener(
            topics = "vitals-events",
            groupId = "analytics-service"
    )
    public void consume(byte[] payload)
            throws InvalidProtocolBufferException {

        VitalsRecordedEvent event =
                VitalsRecordedEvent.parseFrom(payload);

        analyticsService.saveVital(event);
    }
}