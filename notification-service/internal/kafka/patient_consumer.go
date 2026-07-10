package kafka

import (
	"context"

	"github.com/haridev-eng/patient-management/proto/gen/go/events"
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

				event.GetPatientId(),

				event.GetFirstName(),

				event.GetLastName(),

				event.GetEmail(),

				event.GetPhone(),
			)

		case "PATIENT_UPDATED":

			_ = c.projection.UpdatePatient(

				event.GetPatientId(),

				event.GetFirstName(),

				event.GetLastName(),

				event.GetEmail(),

				event.GetPhone(),
			)

		case "PATIENT_DELETED":

			_ = c.projection.DeletePatient(
				event.GetPatientId(),
			)
		}
	}
}
