package workers

import (
	"context"
	"encoding/json"
	"fmt"

	"github.com/google/uuid"
	amqp "github.com/rabbitmq/amqp091-go"

	"github.com/haridev-eng/patient-management/notification-service/internal/notification/sender"
	"github.com/haridev-eng/patient-management/notification-service/internal/rabbitmq"
	"github.com/haridev-eng/patient-management/notification-service/internal/repository"
)

type EmailWorker struct {
	sender       sender.Sender
	deliveryRepo *repository.NotificationDeliveryRepository
}

func NewEmailWorker(
	s sender.Sender,
	deliveryRepo *repository.NotificationDeliveryRepository,
) *EmailWorker {

	return &EmailWorker{
		sender:       s,
		deliveryRepo: deliveryRepo,
	}
}

func (w *EmailWorker) Handle(
	ctx context.Context,
	msg amqp.Delivery,
) error {

	var request rabbitmq.EmailMessage

	if err := json.Unmarshal(msg.Body, &request); err != nil {
		return fmt.Errorf("failed to unmarshal email message: %w", err)
	}

	deliveryID, err := uuid.Parse(request.DeliveryID)
	if err != nil {
		return fmt.Errorf("invalid delivery id: %w", err)
	}

	if err := w.deliveryRepo.MarkAsProcessing(deliveryID); err != nil {
		return fmt.Errorf("failed to mark delivery processing: %w", err)
	}

	err = w.sender.Send(
		ctx,
		sender.EmailRequest{
			Recipient: request.Recipient,
			Subject:   request.Subject,
			Body:      request.Body,
		},
	)

	if err != nil {

		if markErr := w.deliveryRepo.MarkAsFailed(
			deliveryID,
			err.Error(),
		); markErr != nil {

			return fmt.Errorf(
				"send email failed: %v; failed updating delivery: %w",
				err,
				markErr,
			)
		}

		return err
	}

	// For MockSender we don't have a provider message id yet.
	if err := w.deliveryRepo.MarkAsSent(
		deliveryID,
		"",
	); err != nil {

		return fmt.Errorf("failed to mark delivery sent: %w", err)
	}

	return nil
}
