package controllers

import (
	"backend/dto"
	"backend/services"
	"net/http"

	"github.com/gin-gonic/gin"
)

type WatchlistController struct {
	watchlistService *services.WatchlistService
}

func NewWatchlistController() *WatchlistController {
	return &WatchlistController{
		watchlistService: services.NewWatchlistService(),
	}
}

func (ctrl *WatchlistController) Add(c *gin.Context) {
	userID := c.MustGet("userID").(uint)
	var req dto.WatchlistRequest

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	if err := ctrl.watchlistService.AddToWatchlist(userID, req.CoinID); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Koin berhasil ditambahkan ke watchlist"})
}

func (ctrl *WatchlistController) Remove(c *gin.Context) {
	userID := c.MustGet("userID").(uint)
	coinID := c.Param("coinID")

	if coinID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Coin ID diperlukan"})
		return
	}

	if err := ctrl.watchlistService.RemoveFromWatchlist(userID, coinID); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"message": "Koin berhasil dihapus dari watchlist"})
}

func (ctrl *WatchlistController) Get(c *gin.Context) {
	userID := c.MustGet("userID").(uint)

	coinIDs, err := ctrl.watchlistService.GetUserWatchlist(userID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"watchlist": coinIDs})
}
