package outbox

import (
	"time"

	"github.com/google/uuid"
	"gorm.io/gorm"
)

type OutboxRepository struct {
	db *gorm.DB
}

func NewRepository(
	db *gorm.DB,
) *OutboxRepository {

	return &OutboxRepository{
		db: db,
	}
}

func (r *OutboxRepository) WithTx(
	tx *gorm.DB,
) *OutboxRepository {

	return &OutboxRepository{
		db: tx,
	}
}

func (r *OutboxRepository) Create(
	event *OutboxEvent,
) error {

	return r.db.Create(event).Error
}

func (r *OutboxRepository) FindPendingAndFailed(
	limit int,
) (
	[]OutboxEvent,
	error,
) {

	var events []OutboxEvent
	const MaxRetryCount = 5

	err := r.db.
		Where(
			"(status = ? OR status = ?) AND retry_count < ?",
			StatusPending,
			StatusFailed,
			MaxRetryCount,
		).
		Order("retry_count ASC").
		Order("created_at ASC").
		Limit(limit).
		Find(&events).
		Error

	return events, err
}

func (r *OutboxRepository) FindByID(
	id uuid.UUID,
) (
	*OutboxEvent,
	error,
) {

	var event OutboxEvent

	err := r.db.
		First(&event, "id = ?", id).
		Error

	if err != nil {
		return nil, err
	}

	return &event, nil
}

func (r *OutboxRepository) MarkPublished(
	id uuid.UUID,
) error {

	now := time.Now()

	return r.db.
		Model(&OutboxEvent{}).
		Where("id = ?", id).
		Updates(map[string]interface{}{
			"status":        StatusPublished,
			"published_at":  &now,
			"error_message": "",
		}).
		Error
}

func (r *OutboxRepository) MarkFailed(
	id uuid.UUID,
	errMessage string,
) error {

	return r.db.
		Model(&OutboxEvent{}).
		Where("id = ?", id).
		Updates(map[string]interface{}{
			"status":        StatusFailed,
			"error_message": errMessage,
			"retry_count":   gorm.Expr("retry_count + 1"),
		}).
		Error
}
