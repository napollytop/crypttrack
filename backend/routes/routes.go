package routes

import (
	"backend/controllers"
	"backend/middleware"

	"github.com/gin-gonic/gin"
)

func SetupRoutes(router *gin.Engine) {

	authController := controllers.NewAuthController()
	profileController := controllers.NewProfileController()
	watchlistController := controllers.NewWatchlistController()

	// Public
	router.POST("/api/register", authController.Register)
	router.POST("/api/login", authController.Login)

	// Protected
	protected := router.Group("/api")
	protected.Use(middleware.JWTAuthMiddleware())
	{
		protected.GET("/profile", profileController.GetProfile)

		// Watchlist
		protected.GET("/watchlist", watchlistController.Get)
		protected.POST("/watchlist", watchlistController.Add)
		protected.DELETE("/watchlist/:coinID", watchlistController.Remove)
	}
}
