package repository

import (
	"time"

	"github.com/google/uuid"
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

	return r.db.Create(notification).Error
}

func (r *NotificationRepository) FindAll() (
	[]model.Notification,
	error,
) {

	var notifications []model.Notification

	err := r.db.Find(&notifications).Error

	return notifications, err
}

func (r *NotificationRepository) FindByID(
	id uuid.UUID,
) (
	*model.Notification,
	error,
) {

	var notification model.Notification

	err := r.db.First(
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

	return r.db.Save(notification).Error
}

func (r *NotificationRepository) UpdateStatus(
	id uuid.UUID,
	status model.NotificationStatus,
) error {

	return r.db.
		Model(&model.Notification{}).
		Where("id = ?", id).
		Update("status", status).
		Error
}

func (r *NotificationRepository) MarkAsSent(
	id uuid.UUID,
) error {

	now := time.Now()

	return r.db.
		Model(&model.Notification{}).
		Where("id = ?", id).
		Updates(map[string]interface{}{
			"status":  model.StatusSent,
			"sent_at": &now,
		}).Error
}

func (r *NotificationRepository) MarkAsProcessing(
	id uuid.UUID,
) error {

	return r.db.
		Model(&model.Notification{}).
		Where("id = ?", id).
		Update("status", model.StatusProcessing).
		Error
}

func (r *NotificationRepository) MarkAsFailed(
	id uuid.UUID,
	errorMessage string,
) error {

	return r.db.
		Model(&model.Notification{}).
		Where("id = ?", id).
		Updates(map[string]interface{}{
			"status":        model.StatusFailed,
			"error_message": errorMessage,
			"retry_count":   gorm.Expr("retry_count + 1"),
		}).Error
}
