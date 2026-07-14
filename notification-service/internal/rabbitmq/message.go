package rabbitmq

type EmailMessage struct {
	NotificationID string `json:"notification_id"`
	Recipient      string `json:"recipient"`
	Subject        string `json:"subject"`
	Body           string `json:"body"`
}

type SMSMessage struct {
	NotificationID string `json:"notification_id"`
	Phone          string `json:"phone"`
	Message        string `json:"message"`
}

type InAppMessage struct {
	NotificationID string `json:"notification_id"`
	UserID         string `json:"user_id"`
	Title          string `json:"title"`
	Message        string `json:"message"`
}
