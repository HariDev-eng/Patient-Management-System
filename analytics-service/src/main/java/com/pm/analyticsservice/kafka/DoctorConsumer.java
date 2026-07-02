//package com.pm.analyticsservice.kafka;
//
//import com.pm.analyticsservice.service.AnalyticsService;
//import com.pm.events.DoctorCreatedEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class DoctorConsumer {
//
//    private final AnalyticsService analyticsService;
//
//    @KafkaListener(
//            topics = "doctor-events",
//            groupId = "analytics-service"
//    )
//    public void consume(
//            DoctorCreatedEvent event) {
//
//        analyticsService.saveDoctor(event);
//    }
//}