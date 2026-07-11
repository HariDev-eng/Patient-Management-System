package kafka

import (
	"context"

	"github.com/haridev-eng/patient-management/notification-service/internal/dto"
	"github.com/haridev-eng/patient-management/proto/gen/events"
	"google.golang.org/protobuf/proto"

	"github.com/haridev-eng/patient-management/notification-service/internal/projection"
)

type PatientConsumer struct {
	reader *Consumer

	projection *projection.PatientProjection
}

func NewPatientConsumer(

	reader *Consumer,

	projection *projection.PatientProjection,

) *PatientConsumer {

	return &PatientConsumer{
		reader:     reader,
		projection: projection,
	}
}

func (c *PatientConsumer) Start(
	ctx context.Context,
) {

	for {

		msg, err :=
			c.reader.ReadMessage(ctx)

		if err != nil {
			continue
		}

		var event events.PatientEvent

		if err := proto.Unmarshal(
			msg.Value,
			&event,
		); err != nil {

			continue
		}

		switch event.GetEventType() {

		case "PATIENT_CREATED":

			_ = c.projection.CreatePatient(
				dto.PatientProjectionRequest{
					ID:         event.GetPatientId(),
					FirstName:  event.GetFirstName(),
					LastName:   event.GetLastName(),
					Email:      event.GetEmail(),
					Phone:      event.GetPhone(),
					Gender:     event.GetGender(),
					BloodGroup: event.GetBloodGroup(),
				},
			)

		case "PATIENT_UPDATED":

			_ = c.projection.UpdatePatient(
				dto.PatientProjectionRequest{
					ID:         event.GetPatientId(),
					FirstName:  event.GetFirstName(),
					LastName:   event.GetLastName(),
					Email:      event.GetEmail(),
					Phone:      event.GetPhone(),
					Gender:     event.GetGender(),
					BloodGroup: event.GetBloodGroup(),
				},
			)

		case "PATIENT_DELETED":

			_ = c.projection.DeletePatient(
				event.GetPatientId(),
			)
		}
	}
}
