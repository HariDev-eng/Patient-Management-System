package kafka

import (
	"context"
)

type Manager struct {
	consumers []*Consumer
}

func NewManager() *Manager {

	return &Manager{}
}

func (m *Manager) Register(
	consumer *Consumer,
) {

	m.consumers = append(
		m.consumers,
		consumer,
	)
}

func (m *Manager) Start(
	ctx context.Context,
) {

	for _, consumer := range m.consumers {

		go consumer.Start(ctx)
	}
}

func (m *Manager) Shutdown() {

	for _, consumer := range m.consumers {

		_ = consumer.Close()
	}
}
