package dispatcher

import (
	"context"

	"github.com/haridev-eng/patient-management/notification-service/internal/builder"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"github.com/haridev-eng/patient-management/notification-service/internal/rabbitmq"
	"github.com/haridev-eng/patient-management/notification-service/internal/repository"
)

type Dispatcher struct {
	builder    *builder.NotificationBuilder
	repository *repository.NotificationRepository
	publisher  *rabbitmq.Publisher
}

func NewDispatcher(
	builder *builder.NotificationBuilder,
	repository *repository.NotificationRepository,
	publisher *rabbitmq.Publisher,
) *Dispatcher {

	return &Dispatcher{
		builder:    builder,
		repository: repository,
		publisher:  publisher,
	}
}
func (d *Dispatcher) Dispatch(
	request builder.NotificationRequest,
) error {

	for _, channel := range request.Channels {

		notification :=
			d.builder.Build(
				request,
				channel,
			)

		if err := d.repository.Save(notification); err != nil {
			return err
		}

		switch channel {

		case model.ChannelEmail:

			msg := rabbitmq.EmailMessage{
				NotificationID: notification.ID.String(),
				Recipient:      notification.Recipient,
				Subject:        notification.Subject,
				Body:           notification.Message,
			}

			if err := d.publisher.Publish(
				context.Background(),
				rabbitmq.RoutingEmail,
				msg,
			); err != nil {
				return err
			}

		case model.ChannelSMS:

			msg := rabbitmq.SMSMessage{
				NotificationID: notification.ID.String(),
				Phone:          notification.Recipient,
				Message:        notification.Message,
			}

			if err := d.publisher.Publish(
				context.Background(),
				rabbitmq.RoutingSMS,
				msg,
			); err != nil {
				return err
			}

		case model.ChannelInApp:

			msg := rabbitmq.InAppMessage{
				NotificationID: notification.ID.String(),
				UserID:         notification.ReferenceID,
				Title:          notification.Subject,
				Message:        notification.Message,
			}

			if err := d.publisher.Publish(
				context.Background(),
				rabbitmq.RoutingInApp,
				msg,
			); err != nil {
				return err
			}
		}
	}
	return nil
}
