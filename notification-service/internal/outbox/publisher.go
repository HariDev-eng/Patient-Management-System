package outbox

import (
	"context"

	"github.com/haridev-eng/patient-management/notification-service/internal/messaging"
)

type Publisher struct {
	repository *OutboxRepository
	publisher  messaging.Publisher
}

func NewPublisher(
	repository *OutboxRepository,
	publisher messaging.Publisher,
) *Publisher {

	return &Publisher{
		repository: repository,
		publisher:  publisher,
	}
}

func (p *Publisher) PublishPending(
	ctx context.Context,
	limit int,
) error {

	events, err :=
		p.repository.FindPendingAndFailed(limit)

	if err != nil {
		return err
	}

	for _, event := range events {

		if err := p.publishEvent(
			ctx,
			event,
		); err != nil {

			continue
		}
	}

	return nil
}

func (p *Publisher) publishEvent(
	ctx context.Context,
	event OutboxEvent,
) error {

	id := event.ID

	err := p.publisher.PublishRaw(
		ctx,
		event.Destination,
		event.Payload,
	)

	if err != nil {

		_ = p.repository.MarkFailed(
			id,
			err.Error(),
		)

		return err
	}

	return p.repository.MarkPublished(id)
}
