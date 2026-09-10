package rabbitmq

type EmailMessage struct {
	DeliveryID string `json:"delivery_id"`

	NotificationID string `json:"notification_id"`

	Recipient string `json:"recipient"`

	Subject string `json:"subject"`

	Body string `json:"body"`
}

type SMSMessage struct {
	DeliveryID string `json:"delivery_id"`

	Phone string `json:"phone"`

	Message string `json:"message"`

	NotificationID string `json:"notification_id"`
}

type InAppMessage struct {
	DeliveryID string `json:"delivery_id"`

	UserID string `json:"user_id"`

	Title string `json:"title"`

	Message string `json:"message"`

	NotificationID string `json:"notification_id"`
}
