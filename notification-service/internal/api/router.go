package api

import (
	"github.com/gin-gonic/gin"

	"github.com/haridev-eng/patient-management/notification-service/internal/repository"
	"github.com/haridev-eng/patient-management/notification-service/internal/service"
)

func SetupRouter() *gin.Engine {

	router := gin.Default()

	repository := repository.NewNotificationRepository()

	service := service.NewNotificationService(repository)

	handler := NewNotificationHandler(service)

	v1 := router.Group("/api/v1")
	{
		v1.GET("/health", HealthHandler)

		v1.POST("/notifications", handler.CreateNotification)
		v1.GET("/notifications", handler.GetAllNotifications)
		v1.GET("/notifications/:id", handler.GetNotificationByID)
	}

	return router
}
