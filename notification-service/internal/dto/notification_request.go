package dto

type CreateNotificationRequest struct {
	Recipient   string `json:"recipient" binding:"required"`
	Channel     string `json:"channel" binding:"required"`
	EventType   string `json:"eventType" binding:"required"`
	ReferenceID string `json:"referenceId"`

	Subject string `json:"subject"`
	Message string `json:"message" binding:"required"`
}
