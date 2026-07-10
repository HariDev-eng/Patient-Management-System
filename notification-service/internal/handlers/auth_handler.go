package handlers

import (
	"github.com/haridev-eng/patient-management/notification-service/internal/dispatcher"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"github.com/haridev-eng/patient-management/notification-service/internal/template"
)

type AuthNotificationRequest struct {
	Email string
	Name  string
	Link  string
}

type AuthHandler struct {
	*BaseHandler
}

func NewAuthHandler(
	dispatcher *dispatcher.Dispatcher,
) *AuthHandler {

	return &AuthHandler{
		BaseHandler: NewBaseHandler(dispatcher),
	}
}

func (h *AuthHandler) Welcome(
	req AuthNotificationRequest,
) error {

	t := template.Welcome(
		req.Name,
	)

	return h.Dispatch(
		req.Email,
		"WELCOME",
		"",
		[]model.NotificationChannel{
			model.ChannelEmail,
			model.ChannelInApp,
		},
		t,
	)
}

func (h *AuthHandler) PasswordReset(
	req AuthNotificationRequest,
) error {

	t := template.PasswordReset(
		req.Name,
		req.Link,
	)

	return h.Dispatch(
		req.Email,
		"PASSWORD_RESET",
		"",
		[]model.NotificationChannel{
			model.ChannelEmail,
		},
		t,
	)
}
