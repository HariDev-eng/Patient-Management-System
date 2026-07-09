package api

import (
	"github.com/gin-gonic/gin"
	"gorm.io/gorm"

	"github.com/haridev-eng/patient-management/notification-service/internal/repository"
	"github.com/haridev-eng/patient-management/notification-service/internal/service"
)

func SetupRouter(db *gorm.DB) *gin.Engine {

	router := gin.Default()

	notificationRepository := repository.NewNotificationRepository(db)

	notificationService := service.NewNotificationService(notificationRepository)

	handler := NewNotificationHandler(notificationService)

	v1 := router.Group("/api/v1")
	{
		v1.GET("/health", HealthHandler)

		v1.POST("/notifications", handler.CreateNotification)
		v1.GET("/notifications", handler.GetAllNotifications)
		v1.GET("/notifications/:id", handler.GetNotificationByID)
	}

	return router
}
