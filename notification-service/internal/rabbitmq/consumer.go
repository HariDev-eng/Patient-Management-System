package rabbitmq

import (
	"context"
	"fmt"

	amqp "github.com/rabbitmq/amqp091-go"
)

type Handler interface {
	Handle(
		ctx context.Context,
		msg amqp.Delivery,
	) error
}

type Consumer struct {
	client *Client
	queue  string
}

func NewConsumer(
	client *Client,
	queue string,
) *Consumer {

	return &Consumer{
		client: client,
		queue:  queue,
	}
}

func (c *Consumer) Start(
	ctx context.Context,
	handler Handler,
) error {

	msgs, err := c.client.Channel().Consume(

		c.queue,

		"",

		false, // manual ack

		false,

		false,

		false,

		nil,
	)

	if err != nil {
		return fmt.Errorf(
			"consume failed: %w",
			err,
		)
	}

	for {

		select {

		case <-ctx.Done():
			return nil

		case msg, ok := <-msgs:

			if !ok {
				return fmt.Errorf(
					"consumer channel closed",
				)
			}

			if err := handler.Handle(
				ctx,
				msg,
			); err != nil {

				_ = msg.Nack(
					false,
					true,
				)

				continue
			}

			_ = msg.Ack(false)
		}
	}
}
