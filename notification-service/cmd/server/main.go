package main

import (
	"context"

	"github.com/haridev-eng/patient-management/notification-service/internal/api"
	"github.com/haridev-eng/patient-management/notification-service/internal/builder"
	config "github.com/haridev-eng/patient-management/notification-service/internal/configs"
	"github.com/haridev-eng/patient-management/notification-service/internal/db"
	"github.com/haridev-eng/patient-management/notification-service/internal/dispatcher"
	"github.com/haridev-eng/patient-management/notification-service/internal/logger"
	"github.com/haridev-eng/patient-management/notification-service/internal/notification/sender"
	"github.com/haridev-eng/patient-management/notification-service/internal/rabbitmq"
	"github.com/haridev-eng/patient-management/notification-service/internal/repository"
	"github.com/haridev-eng/patient-management/notification-service/internal/workers"
)

func main() {

	cfg := config.LoadConfig()

	logger.Init()
	defer logger.Sync()

	db.Connect(cfg)

	router := api.SetupRouter(db.DB)

	logger.Log.Info(
		"Notification Service Started",
	)

	rabbitcfg := rabbitmq.NewConfig()

	client := rabbitmq.NewClient(rabbitcfg)

	if err := client.Initialize(); err != nil {
		logger.Log.Fatal(err.Error())
	}

	notificationRepository :=
		repository.NewNotificationRepository(db.DB)

	publisher := rabbitmq.NewPublisher(client)

	notificationBuilder :=
		builder.NewNotificationBuilder()

	notificationDispatcher :=
		dispatcher.NewDispatcher(
			notificationBuilder,
			notificationRepository,
			publisher,
		)

	mockSender := sender.NewMockSender()

	emailWorker :=
		workers.NewEmailWorker(
			mockSender,
			notificationRepository,
		)

	ctx := context.Background()

	emailConsumer := rabbitmq.NewConsumer(
		client,
		rabbitmq.EmailQueue,
	)

	go func() {

		if err := emailConsumer.Start(
			ctx,
			emailWorker,
		); err != nil {

			logger.Log.Fatal(err.Error())
		}

	}()

	if err := router.Run(":" + cfg.Port); err != nil {
		logger.Log.Fatal(err.Error())
	}
}
