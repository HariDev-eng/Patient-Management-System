package src.main.java.com.pm.prescriptiosvc.kafka;

import events.PrescriptionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrescriptionProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    public void publishPrescriptionCreated(PrescriptionEvent event) {
        publish(event);
    }

    public void publishPrescriptionUpdated(PrescriptionEvent event) {
        publish(event);
    }

    public void publishPrescriptionDeleted(PrescriptionEvent event) {
        publish(event);
    }

    private void publish(PrescriptionEvent event) {

        kafkaTemplate.send(
                "prescription-events",
                event.getPrescriptionId(),
                event.toByteArray());
    }
}