package model

import (
	"time"

	"github.com/google/uuid"
)

type Patient struct {
	ID uuid.UUID `gorm:"type:uuid;primaryKey"`

	FirstName string

	LastName string

	Email string

	Phone string

	Gender string

	BloodGroup string

	CreatedAt time.Time

	UpdatedAt time.Time
}
