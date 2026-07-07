package db

import (
	"fmt"

	config "github.com/haridev-eng/patient-management/notification-service/internal/configs"
	"github.com/haridev-eng/patient-management/notification-service/internal/logger"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"go.uber.org/zap"
	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

var DB *gorm.DB

func Connect(cfg *config.Config) {

	dsn := fmt.Sprintf(
		"host=%s port=%s user=%s password=%s dbname=%s sslmode=disable",
		cfg.DBHost,
		cfg.DBPort,
		cfg.DBUser,
		cfg.DBPassword,
		cfg.DBName,
	)

	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})

	if err != nil {
		logger.Log.Fatal(
			"Failed to connect to PostgreSQL",
			zap.Error(err),
		)
	}

	DB = db

	err = DB.AutoMigrate(
		&model.Notification{},
	)

	if err != nil {
		logger.Log.Fatal(
			"Migration failed",
			zap.Error(err),
		)
	}

	logger.Log.Info("Connected to PostgreSQL")
}
