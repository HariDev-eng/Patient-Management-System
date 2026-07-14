package rabbitmq

import (
	"os"
)

type Config struct {
	URL          string
	Exchange     string
	ExchangeType string
}

func NewConfig() *Config {

	url := os.Getenv("RABBITMQ_URL")
	if url == "" {
		url = "amqp://guest:guest@localhost:5672/"
	}

	exchange := os.Getenv("RABBITMQ_EXCHANGE")
	if exchange == "" {
		exchange = "notifications"
	}

	return &Config{
		URL:          url,
		Exchange:     exchange,
		ExchangeType: "topic",
	}
}
