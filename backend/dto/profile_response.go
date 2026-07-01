package dto

type ProfileResponse struct {
	ID       uint   `json:"id"`
	Name     string `json:"name"`
	Email    string `json:"email"`
	PhotoURL string `json:"photo_url"`
}