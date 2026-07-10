package handlers

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/dispatcher"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"github.com/haridev-eng/patient-management/notification-service/internal/template"
)

type BillingNotificationRequest struct {
	PatientEmail string
	PatientName  string
	BillID       string
	Amount       float64
}

type BillingHandler struct {
	*BaseHandler
}

func NewBillingHandler(
	dispatcher *dispatcher.Dispatcher,
) *BillingHandler {

	return &BillingHandler{
		BaseHandler: NewBaseHandler(dispatcher),
	}
}

func (h *BillingHandler) BillPaid(
	req BillingNotificationRequest,
) error {

	t := template.BillPaid(
		req.PatientName,
		req.Amount,
	)

	return h.Dispatch(
		req.PatientEmail,
		"BILL_PAID",
		req.BillID,
		[]model.NotificationChannel{
			model.ChannelEmail,
			model.ChannelInApp,
		},
		t,
	)
}
