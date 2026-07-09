package mapper

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/dto"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
)

func ToResponse(
	notification *model.Notification,
) dto.NotificationResponse {

	return dto.NotificationResponse{
		ID:           notification.ID.String(),
		Recipient:    notification.Recipient,
		Channel:      string(notification.Channel),
		EventType:    notification.EventType,
		ReferenceID:  notification.ReferenceID,
		Subject:      notification.Subject,
		Message:      notification.Message,
		Status:       string(notification.Status),
		RetryCount:   notification.RetryCount,
		ErrorMessage: notification.ErrorMessage,
		CreatedAt:    notification.CreatedAt,
		SentAt:       notification.SentAt,
	}
}

func ToResponseList(
	notifications []model.Notification,
) []dto.NotificationResponse {

	response := make(
		[]dto.NotificationResponse,
		0,
		len(notifications),
	)

	for _, notification := range notifications {

		n := notification

		response = append(
			response,
			ToResponse(&n),
		)
	}

	return response
}
