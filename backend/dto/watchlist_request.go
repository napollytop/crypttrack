package dto

type WatchlistRequest struct {
	CoinID string `json:"coin_id" binding:"required"`
}
