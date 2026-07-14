package rabbitmq

import (
	"fmt"
)

func (c *Client) DeclareQueues() error {

	if c.channel == nil {
		return fmt.Errorf("rabbitmq channel is not initialized")
	}

	queues := []struct {
		Name       string
		RoutingKey string
	}{
		{
			Name:       EmailQueue,
			RoutingKey: RoutingEmail,
		},
		{
			Name:       SMSQueue,
			RoutingKey: RoutingSMS,
		},
		{
			Name:       InAppQueue,
			RoutingKey: RoutingInApp,
		},
	}

	for _, q := range queues {

		queue, err := c.channel.QueueDeclare(

			q.Name,

			true, // durable

			false, // auto-delete

			false, // exclusive

			false, // no-wait

			nil,
		)

		if err != nil {
			return fmt.Errorf(
				"failed to declare queue %s: %w",
				q.Name,
				err,
			)
		}

		err = c.channel.QueueBind(

			queue.Name,

			q.RoutingKey,

			c.config.Exchange,

			false,

			nil,
		)

		if err != nil {
			return fmt.Errorf(
				"failed to bind queue %s: %w",
				q.Name,
				err,
			)
		}
	}

	return nil
}
