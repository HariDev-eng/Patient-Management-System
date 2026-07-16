package outbox

import (
	"encoding/json"
	"fmt"

	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"github.com/haridev-eng/patient-management/notification-service/internal/rabbitmq"
)

type EventBuilder struct{}

func NewEventBuilder() *EventBuilder {
	return &EventBuilder{}
}

func (b *EventBuilder) Build(
	notification *model.Notification,
	delivery *model.NotificationDelivery,
) (*OutboxEvent, error) {

	var (
		payload     []byte
		destination string
		err         error
	)

	switch delivery.Channel {

	case model.ChannelEmail:

		payload, err = json.Marshal(
			rabbitmq.EmailMessage{
				DeliveryID:     delivery.ID.String(),
				NotificationID: notification.ID.String(),
				Recipient:      notification.Recipient,
				Subject:        notification.Subject,
				Body:           notification.Message,
			},
		)

		destination = rabbitmq.RoutingEmail

	case model.ChannelSMS:

		payload, err = json.Marshal(
			rabbitmq.SMSMessage{
				DeliveryID:     delivery.ID.String(),
				NotificationID: notification.ID.String(),
				Phone:          notification.Recipient,
				Message:        notification.Message,
			},
		)

		destination = rabbitmq.RoutingSMS

	case model.ChannelInApp:

		payload, err = json.Marshal(
			rabbitmq.InAppMessage{
				DeliveryID:     delivery.ID.String(),
				NotificationID: notification.ID.String(),
				UserID:         notification.ReferenceID,
				Title:          notification.Subject,
				Message:        notification.Message,
			},
		)

		destination = rabbitmq.RoutingInApp

	default:
		return nil, fmt.Errorf("unsupported channel: %s", delivery.Channel)
	}

	if err != nil {
		return nil, err
	}

	return &OutboxEvent{

		EventType: notification.EventType,

		AggregateID: notification.ID.String(),

		Destination: destination,

		Payload: payload,

		Status: StatusPending,
	}, nil
}
