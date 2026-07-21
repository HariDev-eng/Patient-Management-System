package dto

type AppointmentCreatedEvent struct {
	ID string `json:"id"`

	PatientID string `json:"patient_id"`

	DoctorID string `json:"doctor_id"`

	ScheduledAt string `json:"scheduled_at"`
}
