package bootstrap

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/notification/sender"
	"github.com/haridev-eng/patient-management/notification-service/internal/rabbitmq"
	"github.com/haridev-eng/patient-management/notification-service/internal/repository"
	"github.com/haridev-eng/patient-management/notification-service/internal/workers"
)

type NotificationModule struct {
	EmailWorker *workers.EmailWorker
	Publisher   *rabbitmq.Publisher
}

func NewNotificationModule(
	client *rabbitmq.Client,
	repo *repository.NotificationRepository,
) *NotificationModule {

	mockSender := sender.NewMockSender()

	publisher := rabbitmq.NewPublisher(client)

	emailWorker := workers.NewEmailWorker(
		mockSender,
		repo,
	)

	return &NotificationModule{
		EmailWorker: emailWorker,
		Publisher:   publisher,
	}
}
