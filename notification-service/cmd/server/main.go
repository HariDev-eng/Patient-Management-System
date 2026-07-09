package main

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/api"
	config "github.com/haridev-eng/patient-management/notification-service/internal/configs"
	"github.com/haridev-eng/patient-management/notification-service/internal/db"
	"github.com/haridev-eng/patient-management/notification-service/internal/logger"
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

	if err := router.Run(":" + cfg.Port); err != nil {
		logger.Log.Fatal(err.Error())
	}
}
