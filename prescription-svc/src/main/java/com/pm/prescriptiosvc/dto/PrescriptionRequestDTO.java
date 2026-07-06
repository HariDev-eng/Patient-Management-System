package src.main.java.com.pm.prescriptiosvc.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PrescriptionRequestDTO {

    private UUID patientId;

    private UUID doctorId;

    private UUID diagnosisId;

    private UUID appointmentId;

    private List<PrescriptionItemRequestDTO> items;
}