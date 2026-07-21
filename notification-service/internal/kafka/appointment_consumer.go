package kafka

import (
	"context"
	"encoding/json"
	"fmt"

	"github.com/haridev-eng/patient-management/notification-service/internal/dto"
	"github.com/haridev-eng/patient-management/notification-service/internal/handlers"
	"github.com/haridev-eng/patient-management/notification-service/internal/projection"
)

type AppointmentConsumer struct {
	handler *handlers.AppointmentHandler

	patientProjection *projection.PatientProjection
	doctorProjection  *projection.DoctorProjection
}

func NewAppointmentConsumer(
	handler *handlers.AppointmentHandler,
	patientProjection *projection.PatientProjection,
	doctorProjection *projection.DoctorProjection,
) *AppointmentConsumer {

	return &AppointmentConsumer{
		handler:           handler,
		patientProjection: patientProjection,
		doctorProjection:  doctorProjection,
	}
}

func (c *AppointmentConsumer) Handle(
	ctx context.Context,
	data []byte,
) error {

	var event dto.Event

	if err := json.Unmarshal(data, &event); err != nil {
		return err
	}

	switch event.EventType {

	case AppointmentCreated:
		return c.handleAppointmentCreated(ctx, event.Data)

	case AppointmentCancelled:
		return c.handleAppointmentCancelled(ctx, event.Data)

	case AppointmentRescheduled:
		return c.handleAppointmentRescheduled(ctx, event.Data)

	default:
		return fmt.Errorf("unknown event type: %s", event.EventType)
	}
}

func (c *AppointmentConsumer) handleAppointmentCreated(
	ctx context.Context,
	data []byte,
) error {

	var event dto.AppointmentCreatedEvent

	if err := json.Unmarshal(data, &event); err != nil {
		return err
	}

	patient, err := c.patientProjection.FindByID(event.PatientID)
	if err != nil {
		return fmt.Errorf("patient not found: %w", err)
	}

	doctor, err := c.doctorProjection.FindByID(event.DoctorID)
	if err != nil {
		return fmt.Errorf("doctor not found: %w", err)
	}

	return c.handler.AppointmentCreated(
		handlers.AppointmentNotificationRequest{
			PatientEmail:  patient.Email,
			PatientName:   patient.FullName(),
			DoctorName:    doctor.FullName(),
			AppointmentID: event.ID,
			DateTime:      event.ScheduledAt,
		},
	)
}

func (c *AppointmentConsumer) handleAppointmentCancelled(
	ctx context.Context,
	data []byte,
) error {
	// TODO: Implement in-app notification for cancelled appointments.
	return nil
}

func (c *AppointmentConsumer) handleAppointmentRescheduled(
	ctx context.Context,
	data []byte,
) error {
	// TODO: Implement in-app notification for rescheduled appointments.
	return nil
}
