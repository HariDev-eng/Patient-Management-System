package rabbitmq

const (
	NotificationExchange = "notifications"

	RoutingEmail = "notification.email"

	RoutingSMS = "notification.sms"

	RoutingInApp = "notification.inapp"
)

const (
	EmailQueue = "email.queue"

	SMSQueue = "sms.queue"

	InAppQueue = "inapp.queue"
)
