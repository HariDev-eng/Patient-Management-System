package dispatcher

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/builder"
	"github.com/haridev-eng/patient-management/notification-service/internal/repository"
)

type Dispatcher struct {
	builder    *builder.NotificationBuilder
	repository *repository.NotificationRepository
}

func NewDispatcher(
	builder *builder.NotificationBuilder,
	repository *repository.NotificationRepository,
) *Dispatcher {

	return &Dispatcher{
		builder:    builder,
		repository: repository,
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

		// RabbitMQ Publisher will be called here later
	}

	return nil
}
