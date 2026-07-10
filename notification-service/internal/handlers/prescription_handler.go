package handlers

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/dispatcher"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"github.com/haridev-eng/patient-management/notification-service/internal/template"
)

type PrescriptionNotificationRequest struct {
	PatientEmail   string
	PatientName    string
	DoctorName     string
	PrescriptionID string
}

type PrescriptionHandler struct {
	*BaseHandler
}

func NewPrescriptionHandler(
	dispatcher *dispatcher.Dispatcher,
) *PrescriptionHandler {

	return &PrescriptionHandler{
		BaseHandler: NewBaseHandler(dispatcher),
	}
}

func (h *PrescriptionHandler) PrescriptionCreated(
	req PrescriptionNotificationRequest,
) error {

	t := template.PrescriptionCreated(
		req.PatientName,
		req.DoctorName,
	)

	return h.Dispatch(
		req.PatientEmail,
		"PRESCRIPTION_CREATED",
		req.PrescriptionID,
		[]model.NotificationChannel{
			model.ChannelEmail,
			model.ChannelInApp,
		},
		t,
	)
}
