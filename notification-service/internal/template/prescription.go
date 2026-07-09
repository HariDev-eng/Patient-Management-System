package template

import "fmt"

func PrescriptionCreated(
	patientName string,
	doctorName string,
) NotificationTemplate {

	return NotificationTemplate{
		Subject: "Prescription Available",
		Message: fmt.Sprintf(
			`Hello %s,

Dr. %s has created a new prescription for you.

Please log in to view your prescription.

Patient Management Team`,
			patientName,
			doctorName,
		),
	}
}
