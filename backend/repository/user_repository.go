package repository

import (
	"backend/config"
	"backend/models"
)

type UserRepository struct{}

func NewUserRepository() *UserRepository {
	return &UserRepository{}
}

func (r *UserRepository) Create(user *models.User) error {
	return config.DB.Create(user).Error
}

func (r *UserRepository) FindByEmail(email string) (*models.User, error) {
	var user models.User

	err := config.DB.
		Where("email = ?", email).
		First(&user).Error

	return &user, err
}

func (r *UserRepository) FindByID(id uint) (*models.User, error) {

	var user models.User

	err := config.DB.First(&user, id).Error

	if err != nil {
		return nil, err
	}

	return &user, nil
}