package repository

import (
	"backend/config"
	"backend/models"
)

type WatchlistRepository struct{}

func NewWatchlistRepository() *WatchlistRepository {
	return &WatchlistRepository{}
}

func (r *WatchlistRepository) Create(watchlist *models.Watchlist) error {
	return config.DB.Create(watchlist).Error
}

func (r *WatchlistRepository) Delete(userID uint, coinID string) error {
	return config.DB.Where("user_id = ? AND coin_id = ?", userID, coinID).Delete(&models.Watchlist{}).Error
}

func (r *WatchlistRepository) FindByUserID(userID uint) ([]models.Watchlist, error) {
	var watchlists []models.Watchlist
	err := config.DB.Where("user_id = ?", userID).Find(&watchlists).Error
	return watchlists, err
}

func (r *WatchlistRepository) Exists(userID uint, coinID string) (bool, error) {
	var count int64
	err := config.DB.Model(&models.Watchlist{}).Where("user_id = ? AND coin_id = ?", userID, coinID).Count(&count).Error
	return count > 0, err
}
