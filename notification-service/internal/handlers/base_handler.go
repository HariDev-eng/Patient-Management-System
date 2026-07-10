package handlers

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/builder"
	"github.com/haridev-eng/patient-management/notification-service/internal/dispatcher"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"github.com/haridev-eng/patient-management/notification-service/internal/template"
)

type BaseHandler struct {
	dispatcher *dispatcher.Dispatcher
}

func NewBaseHandler(
	dispatcher *dispatcher.Dispatcher,
) *BaseHandler {

	return &BaseHandler{
		dispatcher: dispatcher,
	}
}

func (h *BaseHandler) Dispatch(
	recipient string,
	eventType string,
	referenceID string,
	channels []model.NotificationChannel,
	t template.NotificationTemplate,
) error {

	request := builder.NotificationRequest{
		Recipient: recipient,

		Channels: channels,

		EventType: eventType,

		ReferenceID: referenceID,

		Subject: t.Subject,

		Message: t.Message,
	}

	return h.dispatcher.Dispatch(request)
}
