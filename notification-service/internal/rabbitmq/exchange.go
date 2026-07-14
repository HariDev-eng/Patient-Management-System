package rabbitmq

import (
	"fmt"
)

func (c *Client) DeclareExchange() error {

	if c.channel == nil {
		return fmt.Errorf("rabbitmq channel is not initialized")
	}

	err := c.channel.ExchangeDeclare(

		c.config.Exchange, // exchange name

		c.config.ExchangeType, // topic

		true, // durable

		false, // auto-delete

		false, // internal

		false, // no-wait

		nil, // arguments
	)

	if err != nil {
		return fmt.Errorf(
			"failed to declare exchange: %w",
			err,
		)
	}

	return nil
}
