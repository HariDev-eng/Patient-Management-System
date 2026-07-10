package handlers

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/dispatcher"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"github.com/haridev-eng/patient-management/notification-service/internal/template"
)

type AppointmentNotificationRequest struct {
	PatientEmail  string
	PatientName   string
	DoctorName    string
	AppointmentID string
	DateTime      string
}

type AppointmentHandler struct {
	*BaseHandler
}

func NewAppointmentHandler(
	dispatcher *dispatcher.Dispatcher,
) *AppointmentHandler {

	return &AppointmentHandler{
		BaseHandler: NewBaseHandler(dispatcher),
	}
}

func (h *AppointmentHandler) AppointmentCreated(
	req AppointmentNotificationRequest,
) error {

	t := template.AppointmentCreated(
		req.PatientName,
		req.DoctorName,
		req.DateTime,
	)

	return h.Dispatch(
		req.PatientEmail,
		"APPOINTMENT_CREATED",
		req.AppointmentID,
		[]model.NotificationChannel{
			model.ChannelEmail,
			model.ChannelInApp,
		},
		t,
	)
}

func (h *AppointmentHandler) AppointmentCancelled(
	req AppointmentNotificationRequest,
) error {

	t := template.AppointmentCancelled(
		req.PatientName,
		req.DoctorName,
		req.DateTime,
	)

	return h.Dispatch(
		req.PatientEmail,
		"APPOINTMENT_CANCELLED",
		req.AppointmentID,
		[]model.NotificationChannel{
			model.ChannelEmail,
			model.ChannelInApp,
		},
		t,
	)
}

func (h *AppointmentHandler) AppointmentRescheduled(
	req AppointmentNotificationRequest,
) error {

	t := template.AppointmentRescheduled(
		req.PatientName,
		req.DoctorName,
		req.DateTime,
	)

	return h.Dispatch(
		req.PatientEmail,
		"APPOINTMENT_RESCHEDULED",
		req.AppointmentID,
		[]model.NotificationChannel{
			model.ChannelEmail,
			model.ChannelInApp,
		},
		t,
	)
}
