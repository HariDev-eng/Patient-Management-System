package template

import "fmt"

func BillPaid(
	patientName string,
	amount float64,
) NotificationTemplate {

	return NotificationTemplate{
		Subject: "Payment Successful",
		Message: fmt.Sprintf(
			`Hello %s,

We have received your payment.

Amount Paid: ₹%.2f

Thank you.

Patient Management Team`,
			patientName,
			amount,
		),
	}
}
