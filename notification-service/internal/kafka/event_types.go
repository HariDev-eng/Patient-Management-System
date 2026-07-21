package kafka

const (
	PatientCreated = "PATIENT_CREATED"
	PatientUpdated = "PATIENT_UPDATED"
	PatientDeleted = "PATIENT_DELETED"

	DoctorCreated = "DOCTOR_CREATED"
	DoctorUpdated = "DOCTOR_UPDATED"
	DoctorDeleted = "DOCTOR_DELETED"

	AppointmentCreated     = "APPOINTMENT_CREATED"
	AppointmentCancelled   = "APPOINTMENT_CANCELLED"
	AppointmentRescheduled = "APPOINTMENT_RESCHEDULED"

	BillingCreated = "BILLING_CREATED"
	BillingPaid    = "BILLING_PAID"

	InventoryLow = "INVENTORY_LOW"

	PrescriptionCreated = "PRESCRIPTION_CREATED"
)
