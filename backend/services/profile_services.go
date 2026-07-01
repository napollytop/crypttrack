package services

import (
	"backend/dto"
	"backend/repository"
)

type ProfileService struct {
	userRepo *repository.UserRepository
}

func NewProfileService() *ProfileService {
	return &ProfileService{
		userRepo: repository.NewUserRepository(),
	}
}

func (s *ProfileService) GetProfile(userID uint) (*dto.ProfileResponse, error) {

	user, err := s.userRepo.FindByID(userID)

	if err != nil {
		return nil, err
	}

	return &dto.ProfileResponse{
		ID:       user.ID,
		Name:     user.Name,
		Email:    user.Email,
		PhotoURL: user.PhotoUrl,
	}, nil
}