package template

import "fmt"

func Welcome(
	name string,
) NotificationTemplate {

	return NotificationTemplate{
		Subject: "Welcome",
		Message: fmt.Sprintf(
			`Hello %s,

Welcome to Patient Management.

We're glad to have you onboard.`,
			name,
		),
	}
}

func PasswordReset(
	name string,
	link string,
) NotificationTemplate {

	return NotificationTemplate{
		Subject: "Password Reset",
		Message: fmt.Sprintf(
			`Hello %s,

Use the link below to reset your password.

%s

This link expires in 15 minutes.`,
			name,
			link,
		),
	}
}
