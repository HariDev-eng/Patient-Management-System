package rabbitmq

import (
	"fmt"

	amqp "github.com/rabbitmq/amqp091-go"
)

type Client struct {
	conn    *amqp.Connection
	channel *amqp.Channel
	config  *Config
}

func NewClient(config *Config) *Client {
	return &Client{
		config: config,
	}
}

func (c *Client) Connect() error {

	conn, err := amqp.Dial(c.config.URL)
	if err != nil {
		return fmt.Errorf("failed to connect to RabbitMQ: %w", err)
	}

	channel, err := conn.Channel()
	if err != nil {
		conn.Close()
		return fmt.Errorf("failed to open RabbitMQ channel: %w", err)
	}

	c.conn = conn
	c.channel = channel

	return nil
}

func (c *Client) Initialize() error {
	if err := c.Connect(); err != nil {
		return err
	}

	if err := c.DeclareExchange(); err != nil {
		return err
	}

	if err := c.DeclareQueues(); err != nil {
		return err
	}

	return nil
}

func (c *Client) Channel() *amqp.Channel {
	return c.channel
}

func (c *Client) Connection() *amqp.Connection {
	return c.conn
}

func (c *Client) Close() error {

	if c.channel != nil {
		_ = c.channel.Close()
	}

	if c.conn != nil {
		return c.conn.Close()
	}

	return nil
}
