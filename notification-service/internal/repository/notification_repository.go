package repository

import (
	"github.com/google/uuid"
	"github.com/haridev-eng/patient-management/notification-service/internal/db"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"gorm.io/gorm"
)

type NotificationRepository struct {
	db *gorm.DB
}

func NewNotificationRepository(db *gorm.DB) *NotificationRepository {
	return &NotificationRepository{
		db: db,
	}
}

func (r *NotificationRepository) Save(
	notification *model.Notification,
) error {

	return db.DB.Create(notification).Error
}

func (r *NotificationRepository) FindAll() (
	[]model.Notification,
	error,
) {

	var notifications []model.Notification

	err := db.DB.Find(&notifications).Error

	return notifications, err
}

func (r *NotificationRepository) FindByID(
	id uuid.UUID,
) (
	*model.Notification,
	error,
) {

	var notification model.Notification

	err := db.DB.First(
		&notification,
		"id = ?",
		id,
	).Error

	if err != nil {
		return nil, err
	}

	return &notification, nil
}

func (r *NotificationRepository) Update(
	notification *model.Notification,
) error {

	return db.DB.Save(notification).Error
}
