package dispatcher

import (
	"context"

	"gorm.io/gorm"

	"github.com/haridev-eng/patient-management/notification-service/internal/builder"
	"github.com/haridev-eng/patient-management/notification-service/internal/outbox"
	"github.com/haridev-eng/patient-management/notification-service/internal/repository"
)

type Dispatcher struct {
	db *gorm.DB

	builder *builder.NotificationBuilder

	outboxBuilder *outbox.EventBuilder

	notificationRepo *repository.NotificationRepository

	deliveryRepo *repository.NotificationDeliveryRepository

	outboxRepo *outbox.OutboxRepository
}

func NewDispatcher(
	db *gorm.DB,
	builder *builder.NotificationBuilder,
	outboxBuilder *outbox.EventBuilder,
	notificationRepo *repository.NotificationRepository,
	deliveryRepo *repository.NotificationDeliveryRepository,
	outboxRepo *outbox.OutboxRepository,
) *Dispatcher {

	return &Dispatcher{
		db:               db,
		builder:          builder,
		outboxBuilder:    outboxBuilder,
		notificationRepo: notificationRepo,
		deliveryRepo:     deliveryRepo,
		outboxRepo:       outboxRepo,
	}
}

func (d *Dispatcher) Dispatch(
	ctx context.Context,
	req builder.NotificationRequest,
) error {

	return d.db.Transaction(func(tx *gorm.DB) error {

		notificationRepo := d.notificationRepo.WithTx(tx)
		deliveryRepo := d.deliveryRepo.WithTx(tx)
		outboxRepo := d.outboxRepo.WithTx(tx)

		aggregate := d.builder.Build(req)

		if err := notificationRepo.Create(
			aggregate.Notification,
		); err != nil {
			return err
		}

		for _, delivery := range aggregate.Deliveries {

			delivery.NotificationID =
				aggregate.Notification.ID

			if err := deliveryRepo.Create(
				delivery,
			); err != nil {
				return err
			}

			event, err := d.outboxBuilder.Build(
				aggregate.Notification,
				delivery,
			)

			if err != nil {
				return err
			}

			if err := outboxRepo.Create(
				event,
			); err != nil {
				return err
			}
		}

		return nil
	})
}
