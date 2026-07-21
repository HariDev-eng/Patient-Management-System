package projection

import (
	"github.com/google/uuid"

	"github.com/haridev-eng/patient-management/notification-service/internal/dto"
	"github.com/haridev-eng/patient-management/notification-service/internal/model"
	"github.com/haridev-eng/patient-management/notification-service/internal/repository"
)

type DoctorProjection struct {
	repository *repository.DoctorRepository
}

func NewDoctorProjection(
	repository *repository.DoctorRepository,
) *DoctorProjection {

	return &DoctorProjection{
		repository: repository,
	}
}

func (p *DoctorProjection) CreateDoctor(
	req dto.DoctorProjectionRequest,
) error {

	return p.repository.Create(
		&model.Doctor{
			ID:         uuid.MustParse(req.ID),
			FirstName:  req.FirstName,
			LastName:   req.LastName,
			Email:      req.Email,
			Speciality: req.Speciality,
		},
	)
}

func (p *DoctorProjection) UpdateDoctor(
	req dto.DoctorProjectionRequest,
) error {

	doctor, err := p.repository.FindByID(
		uuid.MustParse(req.ID),
	)

	if err != nil {
		return err
	}

	doctor.FirstName = req.FirstName
	doctor.LastName = req.LastName
	doctor.Email = req.Email
	doctor.Speciality = req.Speciality

	return p.repository.Update(doctor)
}

func (p *DoctorProjection) DeleteDoctor(
	id string,
) error {

	return p.repository.Delete(
		uuid.MustParse(id),
	)
}

func (p *DoctorProjection) FindByID(
	id string,
) (*model.Doctor, error) {

	return p.repository.FindByID(
		uuid.MustParse(id),
	)
}
