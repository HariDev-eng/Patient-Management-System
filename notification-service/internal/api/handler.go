package api

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"github.com/haridev-eng/patient-management/notification-service/internal/mapper"

	"github.com/haridev-eng/patient-management/notification-service/internal/dto"
	"github.com/haridev-eng/patient-management/notification-service/internal/service"
)

type NotificationHandler struct {
	service *service.NotificationService
}

func NewNotificationHandler(
	service *service.NotificationService,
) *NotificationHandler {

	return &NotificationHandler{
		service: service,
	}
}

func HealthHandler(c *gin.Context) {

	c.JSON(http.StatusOK, gin.H{
		"status":  "UP",
		"service": "notification-service",
		"version": "1.0.0",
	})
}

func (h *NotificationHandler) CreateNotification(
	c *gin.Context,
) {

	var request dto.CreateNotificationRequest

	if err := c.ShouldBindJSON(&request); err != nil {

		c.JSON(http.StatusBadRequest, gin.H{
			"error": err.Error(),
		})

		return
	}

	notification, err :=
		h.service.CreateNotification(request)

	if err != nil {

		c.JSON(http.StatusInternalServerError, gin.H{
			"error": err.Error(),
		})

		return
	}

	c.JSON(
		http.StatusCreated,
		mapper.ToResponse(notification))
}

func (h *NotificationHandler) GetAllNotifications(
	c *gin.Context,
) {

	notifications, err :=
		h.service.GetAllNotifications()

	if err != nil {

		c.JSON(http.StatusInternalServerError, gin.H{
			"error": err.Error(),
		})

		return
	}

	c.JSON(
		http.StatusOK,
		mapper.ToResponseList(notifications),
	)
}

func (h *NotificationHandler) GetNotificationByID(
	c *gin.Context,
) {

	id, err := uuid.Parse(c.Param("id"))

	if err != nil {

		c.JSON(http.StatusBadRequest, gin.H{
			"error": "Invalid notification id",
		})

		return
	}

	notification, err :=
		h.service.GetNotificationByID(id)

	if err != nil {

		c.JSON(http.StatusNotFound, gin.H{
			"error": "Notification not found",
		})

		return
	}

	c.JSON(
		http.StatusOK,
		mapper.ToResponse(notification),
	)
}
