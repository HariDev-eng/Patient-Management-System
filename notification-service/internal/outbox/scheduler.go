package outbox

import (
	"context"
	"log"
	"time"
)

type Scheduler struct {
	publisher *Publisher
	interval  time.Duration
}

func NewScheduler(
	publisher *Publisher,
	interval time.Duration,
) *Scheduler {

	return &Scheduler{
		publisher: publisher,
		interval:  interval,
	}
}

func (s *Scheduler) Start(
	ctx context.Context,
) {

	ticker := time.NewTicker(
		s.interval,
	)

	defer ticker.Stop()

	for {

		select {

		case <-ctx.Done():
			return

		case <-ticker.C:

			if err := s.publisher.PublishPending(
				ctx,
				100,
			); err != nil {

				log.Println(
					"Outbox Publisher:",
					err,
				)
			}
		}
	}
}
