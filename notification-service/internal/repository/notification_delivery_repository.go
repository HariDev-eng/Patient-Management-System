package repository

import (
	"time"

	"github.com/google/uuid"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"gorm.io/gorm"
)

type NotificationDeliveryRepository struct {
	db *gorm.DB
}

func NewNotificationDeliveryRepository(
	db *gorm.DB,
) *NotificationDeliveryRepository {

	return &NotificationDeliveryRepository{
		db: db,
	}
}

func (r *NotificationDeliveryRepository) WithTx(
	tx *gorm.DB,
) *NotificationDeliveryRepository {

	return &NotificationDeliveryRepository{
		db: tx,
	}
}

func (r *NotificationDeliveryRepository) Create(
	delivery *model.NotificationDelivery,
) error {

	return r.db.Create(delivery).Error
}

func (r *NotificationDeliveryRepository) FindByID(
	id uuid.UUID,
) (
	*model.NotificationDelivery,
	error,
) {

	var delivery model.NotificationDelivery

	err := r.db.
		Preload("Notification").
		First(&delivery, "id = ?", id).
		Error

	if err != nil {
		return nil, err
	}

	return &delivery, nil
}

func (r *NotificationDeliveryRepository) FindByNotification(
	notificationID uuid.UUID,
) (
	[]model.NotificationDelivery,
	error,
) {

	var deliveries []model.NotificationDelivery

	err := r.db.
		Where("notification_id = ?", notificationID).
		Find(&deliveries).
		Error

	return deliveries, err
}

func (r *NotificationDeliveryRepository) FindFailed() (
	[]model.NotificationDelivery,
	error,
) {

	var deliveries []model.NotificationDelivery

	err := r.db.
		Preload("Notification").
		Where("status = ?", model.DeliveryFailed).
		Find(&deliveries).
		Error

	return deliveries, err
}

func (r *NotificationDeliveryRepository) FindPending() (
	[]model.NotificationDelivery,
	error,
) {

	var deliveries []model.NotificationDelivery

	err := r.db.
		Preload("Notification").
		Where("status = ?", model.DeliveryPending).
		Find(&deliveries).
		Error

	return deliveries, err
}

func (r *NotificationDeliveryRepository) MarkAsProcessing(
	id uuid.UUID,
) error {

	return r.db.
		Model(&model.NotificationDelivery{}).
		Where("id = ?", id).
		Update("status", model.DeliveryProcessing).
		Error
}

func (r *NotificationDeliveryRepository) MarkAsSent(
	id uuid.UUID,
	providerMessageID string,
) error {

	now := time.Now()

	return r.db.
		Model(&model.NotificationDelivery{}).
		Where("id = ?", id).
		Updates(map[string]interface{}{
			"status":              model.DeliverySent,
			"sent_at":             &now,
			"provider_message_id": providerMessageID,
		}).Error
}

func (r *NotificationDeliveryRepository) MarkAsFailed(
	id uuid.UUID,
	errorMessage string,
) error {

	return r.db.
		Model(&model.NotificationDelivery{}).
		Where("id = ?", id).
		Updates(map[string]interface{}{
			"status":        model.DeliveryFailed,
			"error_message": errorMessage,
			"retry_count":   gorm.Expr("retry_count + 1"),
		}).Error
}

func (r *NotificationDeliveryRepository) Update(
	delivery *model.NotificationDelivery,
) error {

	return r.db.Save(delivery).Error
}
