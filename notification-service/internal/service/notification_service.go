package service

import (
	"time"

	"github.com/google/uuid"

	"github.com/haridev-eng/patient-management/notification-service/internal/dto"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"github.com/haridev-eng/patient-management/notification-service/internal/repository"
)

type NotificationService struct {
	repository *repository.NotificationRepository
}

func NewNotificationService(
	repository *repository.NotificationRepository,
) *NotificationService {

	return &NotificationService{
		repository: repository,
	}
}

func (s *NotificationService) CreateNotification(
	request dto.CreateNotificationRequest,
) (*model.Notification, error) {

	notification := &model.Notification{
		Recipient:    request.Recipient,
		Channel:      model.NotificationChannel(request.Channel),
		EventType:    request.EventType,
		ReferenceID:  request.ReferenceID,
		Subject:      request.Subject,
		Message:      request.Message,
		Status:       model.StatusPending,
		RetryCount:   0,
		ErrorMessage: "",
	}

	err := s.repository.Save(notification)

	if err != nil {
		return nil, err
	}

	return notification, nil
}

func (s *NotificationService) GetAllNotifications() (
	[]model.Notification,
	error,
) {

	return s.repository.FindAll()
}

func (s *NotificationService) GetNotificationByID(
	id uuid.UUID,
) (
	*model.Notification,
	error,
) {

	return s.repository.FindByID(id)
}

func (s *NotificationService) MarkAsSent(
	id uuid.UUID,
) error {

	notification, err := s.repository.FindByID(id)

	if err != nil {
		return err
	}

	now := time.Now()

	notification.Status = model.StatusSent
	notification.SentAt = &now

	return s.repository.Update(notification)
}

func (s *NotificationService) MarkAsFailed(
	id uuid.UUID,
	errorMessage string,
) error {

	notification, err := s.repository.FindByID(id)

	if err != nil {
		return err
	}

	notification.Status = model.StatusFailed
	notification.RetryCount++
	notification.ErrorMessage = errorMessage

	return s.repository.Update(notification)
}
