package dto

type DoctorProjectionRequest struct {
	ID string `json:"id"`

	FirstName string `json:"first_name"`

	LastName string `json:"last_name"`

	Email string `json:"email"`

	Speciality string `json:"speciality"`
}
