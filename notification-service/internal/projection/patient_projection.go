package projection

import (
	"github.com/google/uuid"
	"github.com/haridev-eng/patient-management/notification-service/internal/dto"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"github.com/haridev-eng/patient-management/notification-service/internal/repository"
)

type PatientProjection struct {
	repository *repository.PatientRepository
}

func NewPatientProjection(
	repository *repository.PatientRepository,
) *PatientProjection {

	return &PatientProjection{
		repository: repository,
	}
}

func (p *PatientProjection) CreatePatient(req dto.PatientProjectionRequest) error {

	return p.repository.Create(
		&model.Patient{

			ID: uuid.MustParse(req.ID),

			FirstName: req.FirstName,

			LastName: req.LastName,

			Email: req.Email,

			Phone: req.Phone,
		},
	)
}

func (p *PatientProjection) UpdatePatient(req dto.PatientProjectionRequest) error {

	patient, err :=
		p.repository.FindByID(
			uuid.MustParse(req.ID),
		)

	if err != nil {
		return err
	}

	patient.FirstName = req.FirstName
	patient.LastName = req.LastName
	patient.Email = req.Email
	patient.Phone = req.Phone

	return p.repository.Update(patient)
}

func (p *PatientProjection) DeletePatient(
	id string,
) error {

	return p.repository.Delete(
		uuid.MustParse(id),
	)
}
