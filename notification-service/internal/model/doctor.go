package model

import (
	"time"

	"github.com/google/uuid"
)

type Doctor struct {
	ID uuid.UUID `gorm:"type:uuid;primaryKey"`

	FirstName string `gorm:"size:100;not null"`

	LastName string `gorm:"size:100;not null"`

	Email string `gorm:"size:255"`

	Speciality string `gorm:"size:100"`

	CreatedAt time.Time

	UpdatedAt time.Time
}

func (d Doctor) FullName() string {
	return d.FirstName + " " + d.LastName
}
