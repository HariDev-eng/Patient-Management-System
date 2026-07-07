package main

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/api"
	"github.com/haridev-eng/patient-management/notification-service/internal/configs"
	"github.com/haridev-eng/patient-management/notification-service/internal/db"
	"github.com/haridev-eng/patient-management/notification-service/internal/logger"
	"go.uber.org/zap"
)

func main() {

	cfg := config.LoadConfig()

	logger.Init()
	defer logger.Sync()

	db.Connect(cfg)

	router := api.SetupRouter()

	logger.Log.Info(
		"Notification Service started",
		zap.String("service", "notification-service"),
		zap.String("port", cfg.Port),
	)

	if err := router.Run(":" + cfg.Port); err != nil {
		logger.Log.Fatal(
			"Failed to start server",
		)
	}
}
