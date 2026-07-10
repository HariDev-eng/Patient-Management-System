package handlers

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/dispatcher"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"github.com/haridev-eng/patient-management/notification-service/internal/template"
)

type InventoryNotificationRequest struct {
	RecipientEmail string
	InventoryID    string
	ItemName       string
	Quantity       int
}

type InventoryHandler struct {
	*BaseHandler
}

func NewInventoryHandler(
	dispatcher *dispatcher.Dispatcher,
) *InventoryHandler {

	return &InventoryHandler{
		BaseHandler: NewBaseHandler(dispatcher),
	}
}

func (h *InventoryHandler) LowStock(
	req InventoryNotificationRequest,
) error {

	t := template.LowStock(
		req.ItemName,
		req.Quantity,
	)

	return h.Dispatch(
		req.RecipientEmail,
		"INVENTORY_LOW_STOCK",
		req.InventoryID,
		[]model.NotificationChannel{
			model.ChannelEmail,
			model.ChannelInApp,
		},
		t,
	)
}
