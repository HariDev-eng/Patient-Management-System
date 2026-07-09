package template

import "fmt"

func AppointmentCreated(
	patientName string,
	doctorName string,
	dateTime string,
) NotificationTemplate {

	return NotificationTemplate{
		Subject: "Appointment Confirmed",
		Message: fmt.Sprintf(
			`Hello %s,

Your appointment with Dr. %s has been confirmed.

Date & Time: %s

Thank you,
Patient Management Team`,
			patientName,
			doctorName,
			dateTime,
		),
	}
}

func AppointmentCancelled(
	patientName string,
	doctorName string,
	dateTime string,
) NotificationTemplate {

	return NotificationTemplate{
		Subject: "Appointment Cancelled",
		Message: fmt.Sprintf(
			`Hello %s,

Your appointment with Dr. %s scheduled for %s has been cancelled.

Please contact the hospital if you would like to reschedule.

Patient Management Team`,
			patientName,
			doctorName,
			dateTime,
		),
	}
}

func AppointmentRescheduled(
	patientName string,
	doctorName string,
	newDate string,
) NotificationTemplate {

	return NotificationTemplate{
		Subject: "Appointment Rescheduled",
		Message: fmt.Sprintf(
			`Hello %s,

Your appointment with Dr. %s has been rescheduled.

New Date & Time: %s

Patient Management Team`,
			patientName,
			doctorName,
			newDate,
		),
	}
}
