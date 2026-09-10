package repository

import (
	"github.com/google/uuid"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"gorm.io/gorm"
)

type DoctorRepository struct {
	db *gorm.DB
}

func NewDoctorRepository(
	db *gorm.DB,
) *DoctorRepository {

	return &DoctorRepository{
		db: db,
	}
}

func (r *DoctorRepository) Create(
	doctor *model.Doctor,
) error {

	return r.db.Create(doctor).Error
}

func (r *DoctorRepository) FindByID(
	id uuid.UUID,
) (*model.Doctor, error) {

	var doctor model.Doctor

	err := r.db.
		First(&doctor, "id = ?", id).
		Error

	if err != nil {
		return nil, err
	}

	return &doctor, nil
}

func (r *DoctorRepository) FindAll() (
	[]model.Doctor,
	error,
) {

	var doctors []model.Doctor

	err := r.db.Find(&doctors).Error

	return doctors, err
}

func (r *DoctorRepository) Update(
	doctor *model.Doctor,
) error {

	return r.db.Save(doctor).Error
}

func (r *DoctorRepository) Delete(
	id uuid.UUID,
) error {

	return r.db.Delete(
		&model.Doctor{},
		"id = ?",
		id,
	).Error
}
