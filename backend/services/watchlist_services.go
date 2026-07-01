package services

import (
	"backend/models"
	"backend/repository"
)

type WatchlistService struct {
	watchlistRepo *repository.WatchlistRepository
}

func NewWatchlistService() *WatchlistService {
	return &WatchlistService{
		watchlistRepo: repository.NewWatchlistRepository(),
	}
}

func (s *WatchlistService) AddToWatchlist(userID uint, coinID string) error {
	exists, err := s.watchlistRepo.Exists(userID, coinID)
	if err != nil {
		return err
	}
	if exists {
		return nil // Already in watchlist
	}

	watchlist := models.Watchlist{
		UserID: userID,
		CoinID: coinID,
	}
	return s.watchlistRepo.Create(&watchlist)
}

func (s *WatchlistService) RemoveFromWatchlist(userID uint, coinID string) error {
	return s.watchlistRepo.Delete(userID, coinID)
}

func (s *WatchlistService) GetUserWatchlist(userID uint) ([]string, error) {
	watchlists, err := s.watchlistRepo.FindByUserID(userID)
	if err != nil {
		return nil, err
	}

	var coinIDs []string
	for _, w := range watchlists {
		coinIDs = append(coinIDs, w.CoinID)
	}
	return coinIDs, nil
}
