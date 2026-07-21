package kafka

import (
	"context"
	"encoding/json"
	"fmt"

	"github.com/haridev-eng/patient-management/notification-service/internal/dto"
	"github.com/haridev-eng/patient-management/notification-service/internal/projection"
)

type PatientConsumer struct {
	projection *projection.PatientProjection
}

func NewPatientConsumer(
	projection *projection.PatientProjection,
) *PatientConsumer {
	return &PatientConsumer{
		projection: projection,
	}
}

func (c *PatientConsumer) Handle(
	ctx context.Context,
	data []byte,
) error {

	var event dto.Event

	if err := json.Unmarshal(data, &event); err != nil {
		return err
	}

	switch event.EventType {

	case PatientCreated:
		return c.handlePatientCreated(event.Data)

	case PatientUpdated:
		return c.handlePatientUpdated(event.Data)

	case PatientDeleted:
		return c.handlePatientDeleted(event.Data)

	default:
		return fmt.Errorf("unknown event type: %s", event.EventType)
	}
}

func (c *PatientConsumer) handlePatientCreated(data []byte) error {
	var event dto.PatientCreatedEvent

	if err := json.Unmarshal(data, &event); err != nil {
		return err
	}

	return c.projection.CreatePatient(event)
}

func (c *PatientConsumer) handlePatientUpdated(data []byte) error {
	var event dto.PatientUpdatedEvent

	if err := json.Unmarshal(data, &event); err != nil {
		return err
	}

	return c.projection.UpdatePatient(event)
}

func (c *PatientConsumer) handlePatientDeleted(data []byte) error {
	var event dto.PatientDeletedEvent

	if err := json.Unmarshal(data, &event); err != nil {
		return err
	}

	return c.projection.DeletePatient(event.ID)
}
