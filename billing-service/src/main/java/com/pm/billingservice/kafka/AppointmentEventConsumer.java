//package com.pm.billingservice.kafka;
//
//import com.pm.billingservice.service.BillingService;
//import com.pm.events.AppointmentCreatedEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class AppointmentEventConsumer {
//    private final BillingService billingService;
//
//    @KafkaListener(topics = "appointment-created", groupId = "billing-service")
//    public void consume(byte[] payload) throws Exception{
//        AppointmentCreatedEvent event = AppointmentCreatedEvent.parseFrom(payload);
//
//        billingService.createBillingFromAppointment(event);
//    }
//}
