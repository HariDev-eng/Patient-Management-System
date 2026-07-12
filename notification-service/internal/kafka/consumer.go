package kafka

import (
	"context"

	"github.com/segmentio/kafka-go"
)

type Consumer struct {
	reader *kafka.Reader

	handler Handler
}

func NewConsumer(

	cfg *Config,

	topic string,

	handler Handler,

) *Consumer {

	reader := kafka.NewReader(

		kafka.ReaderConfig{

			Brokers: cfg.Brokers,

			GroupID: cfg.GroupID,

			Topic: topic,
		},
	)

	return &Consumer{

		reader: reader,

		handler: handler,
	}
}

func (c *Consumer) Start(
	ctx context.Context,
) {

	for {

		msg, err :=
			c.reader.ReadMessage(ctx)

		if err != nil {
			continue
		}

		_ = c.handler.Handle(
			ctx,
			msg.Value,
		)
	}
}

func (c *Consumer) ReadMessage(
	ctx context.Context,
) (kafka.Message, error) {

	return c.reader.ReadMessage(ctx)
}

func (c *Consumer) Close() error {
	return c.reader.Close()
}
