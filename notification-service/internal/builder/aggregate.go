package builder

import "github.com/haridev-eng/patient-management/notification-service/internal/model"

type NotificationAggregate struct {
	Notification *model.Notification

	Deliveries []*model.NotificationDelivery
}
