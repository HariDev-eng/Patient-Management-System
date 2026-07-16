package builder

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
)

type NotificationBuilder struct{}

func NewNotificationBuilder() *NotificationBuilder {
	return &NotificationBuilder{}
}

func (b *NotificationBuilder) Build(
	request NotificationRequest,
) *NotificationAggregate {

	notification := &model.Notification{
		Recipient:   request.Recipient,
		EventType:   request.EventType,
		ReferenceID: request.ReferenceID,
		Subject:     request.Subject,
		Message:     request.Message,
	}

	deliveries := make([]*model.NotificationDelivery, 0, len(request.Channels))

	for _, channel := range request.Channels {

		deliveries = append(deliveries, &model.NotificationDelivery{
			Channel: channel,
			Status:  model.DeliveryPending,
		})
	}

	return &NotificationAggregate{
		Notification: notification,
		Deliveries:   deliveries,
	}
}
