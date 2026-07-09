package template

import "fmt"

func LowStock(
	itemName string,
	quantity int,
) NotificationTemplate {

	return NotificationTemplate{
		Subject: "Inventory Alert",
		Message: fmt.Sprintf(
			`Inventory Alert

%s is running low.

Current Quantity: %d

Please restock immediately.`,
			itemName,
			quantity,
		),
	}
}
