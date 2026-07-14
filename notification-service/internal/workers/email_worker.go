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
	sender     sender.Sender
	repository *repository.NotificationRepository
}

func NewEmailWorker(
	s sender.Sender,
	r *repository.NotificationRepository,
) *EmailWorker {

	return &EmailWorker{
		sender:     s,
		repository: r,
	}
}

func (w *EmailWorker) Handle(
	ctx context.Context,
	msg amqp.Delivery,
) error {

	// Deserialize RabbitMQ message
	var request rabbitmq.EmailMessage

	if err := json.Unmarshal(msg.Body, &request); err != nil {
		return fmt.Errorf("failed to unmarshal email message: %w", err)
	}

	// Parse Notification ID
	notificationID, err := uuid.Parse(request.NotificationID)
	if err != nil {
		return fmt.Errorf("invalid notification id: %w", err)
	}

	// Mark as processing
	if err := w.repository.MarkAsProcessing(notificationID); err != nil {
		return fmt.Errorf("failed to mark notification as processing: %w", err)
	}

	// Send email
	err = w.sender.Send(
		ctx,
		sender.EmailRequest{
			Recipient: request.Recipient,
			Subject:   request.Subject,
			Body:      request.Body,
		},
	)

	if err != nil {

		// Update status
		if markErr := w.repository.MarkAsFailed(notificationID, err.Error()); markErr != nil {
			return fmt.Errorf(
				"send email failed: %v; additionally failed to update notification status: %w",
				err,
				markErr,
			)
		}

		return fmt.Errorf("failed to send email: %w", err)
	}

	// Update status
	if err := w.repository.MarkAsSent(notificationID); err != nil {
		return fmt.Errorf("failed to mark notification as sent: %w", err)
	}

	return nil
}
