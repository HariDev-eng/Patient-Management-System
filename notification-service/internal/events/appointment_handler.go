package events

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/builder"
	"github.com/haridev-eng/patient-management/notification-service/internal/dispatcher"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"github.com/haridev-eng/patient-management/notification-service/internal/template"
)

type AppointmentHandler struct {
	dispatcher *dispatcher.Dispatcher
}

func NewAppointmentHandler(
	dispatcher *dispatcher.Dispatcher,
) *AppointmentHandler {

	return &AppointmentHandler{
		dispatcher: dispatcher,
	}
}

func (h *AppointmentHandler) AppointmentCreated(
	patientEmail string,
	patientName string,
	doctorName string,
	appointmentID string,
	dateTime string,
) error {

	t := template.AppointmentCreated(
		patientName,
		doctorName,
		dateTime,
	)

	request := builder.NotificationRequest{
		Recipient: patientEmail,

		Channels: []model.NotificationChannel{
			model.ChannelEmail,
			model.ChannelInApp,
		},

		EventType: "APPOINTMENT_CREATED",

		ReferenceID: appointmentID,

		Subject: t.Subject,

		Message: t.Message,
	}

	return h.dispatcher.Dispatch(request)
}

//func (h *AppointmentHandler) AppointmentCancelled(patientEmail string,
//	patientName string,
//	doctorName string,
//	appointmentID string,
//	dateTime string,) error
//
//func (h *AppointmentHandler) AppointmentRescheduled(...) error
