package dto

import "time"

type NotificationResponse struct {
	ID string `json:"id"`

	Recipient string `json:"recipient"`

	Channel string `json:"channel"`

	EventType string `json:"eventType"`

	ReferenceID string `json:"referenceId"`

	Subject string `json:"subject"`

	Message string `json:"message"`

	Status string `json:"status"`

	RetryCount int `json:"retryCount"`

	ErrorMessage string `json:"errorMessage"`

	CreatedAt time.Time `json:"createdAt"`

	SentAt *time.Time `json:"sentAt"`
}
