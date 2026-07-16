package repository

import (
	"github.com/google/uuid"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"gorm.io/gorm"
)

type NotificationRepository struct {
	db *gorm.DB
}

func (r *NotificationRepository) WithTx(
	tx *gorm.DB,
) *NotificationRepository {

	return &NotificationRepository{
		db: tx,
	}
}

func NewNotificationRepository(db *gorm.DB) *NotificationRepository {
	return &NotificationRepository{
		db: db,
	}
}

func (r *NotificationRepository) Create(
	notification *model.Notification,
) error {

	return r.db.Create(notification).Error
}

func (r *NotificationRepository) FindAll() (
	[]model.Notification,
	error,
) {

	var notifications []model.Notification

	err := r.db.
		Preload("Deliveries").
		Find(&notifications).
		Error

	return notifications, err
}

func (r *NotificationRepository) FindByID(
	id uuid.UUID,
) (
	*model.Notification,
	error,
) {

	var notification model.Notification

	err := r.db.
		Preload("Deliveries").
		First(&notification, "id = ?", id).
		Error

	if err != nil {
		return nil, err
	}

	return &notification, nil
}

func (r *NotificationRepository) Update(
	notification *model.Notification,
) error {

	return r.db.Save(notification).Error
}
