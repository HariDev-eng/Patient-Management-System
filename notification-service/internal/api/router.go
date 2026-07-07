package api

import "github.com/gin-gonic/gin"

func SetupRouter() *gin.Engine {

	router := gin.Default()

	v1 := router.Group("/api/v1")
	{
		v1.GET("/health", HealthHandler)
	}

	return router
}
