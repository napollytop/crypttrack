package models

import(
	"gorm.io/gorm"
)

type Watchlist struct{
	gorm.Model

	UserID uint `gorm:"not null"`
	CoinID string `gorm:"not null"`
}