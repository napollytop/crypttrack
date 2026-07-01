package services

import (
	"errors"

	"backend/dto"
	"backend/models"
	"backend/repository"
	"backend/utils"

	"gorm.io/gorm"
)

type AuthService struct {
	userRepo *repository.UserRepository
}

func NewAuthService() *AuthService {
	return &AuthService{
		userRepo: repository.NewUserRepository(),
	}
}

func (s *AuthService) Register(req dto.RegisterRequest) error {

	_, err := s.userRepo.FindByEmail(req.Email)

	if err == nil {
		return errors.New("email sudah digunakan")
	}

	if !errors.Is(err, gorm.ErrRecordNotFound) {
		return err
	}

	hash, err := utils.HashPassword(req.Password)
	if err != nil {
		return err
	}

	user := models.User{
		Name:         req.Name,
		Email:        req.Email,
		PasswordHash: hash,
	}

	return s.userRepo.Create(&user)
}

func (s *AuthService) Login(req dto.LoginRequest) (string, error) {

	user, err := s.userRepo.FindByEmail(req.Email)

	if err != nil {
		return "", errors.New("email atau password salah")
	}

	if err := utils.CheckPassword(req.Password, user.PasswordHash); err != nil {
		return "", errors.New("email atau password salah")
	}

	token, err := utils.GenerateToken(user.ID)

	if err != nil {
		return "", err
	}

	return token, nil
}