package controllers

import (
	"backend/services"
	"net/http"

	"github.com/gin-gonic/gin"
)

type ProfileController struct {
	profileService *services.ProfileService
}

func NewProfileController() *ProfileController {
	return &ProfileController{
		profileService: services.NewProfileService(),
	}
}

func (p *ProfileController) GetProfile(c *gin.Context) {

	userID := c.GetUint("userID")

	profile, err := p.profileService.GetProfile(userID)

	if err != nil {
		c.JSON(http.StatusNotFound, gin.H{
			"error": "User tidak ditemukan",
		})
		return
	}

	c.JSON(http.StatusOK, profile)
}