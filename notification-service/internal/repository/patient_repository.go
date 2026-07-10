package repository

import (
	"github.com/google/uuid"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"gorm.io/gorm"
)

type PatientRepository struct {
	db *gorm.DB
}

func NewPatientRepository(
	db *gorm.DB,
) *PatientRepository {

	return &PatientRepository{
		db: db,
	}
}

func (r *PatientRepository) Create(
	patient *model.Patient,
) error {

	return r.db.Create(patient).Error
}

func (r *PatientRepository) Update(
	patient *model.Patient,
) error {

	return r.db.Save(patient).Error
}

func (r *PatientRepository) Delete(
	id uuid.UUID,
) error {

	return r.db.Delete(
		&model.Patient{},
		id,
	).Error
}

func (r *PatientRepository) FindByID(
	id uuid.UUID,
) (*model.Patient, error) {

	var patient model.Patient

	err := r.db.First(
		&patient,
		"id = ?",
		id,
	).Error

	if err != nil {
		return nil, err
	}

	return &patient, nil
}

func (r *PatientRepository) FindByEmail(
	email string,
) (*model.Patient, error) {

	var patient model.Patient

	err := r.db.First(
		&patient,
		"email = ?",
		email,
	).Error

	if err != nil {
		return nil, err
	}

	return &patient, nil
}
