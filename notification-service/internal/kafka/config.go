package kafka

import (
	"os"
	"strings"
)

type Config struct {
	Brokers []string

	GroupID string
}

func NewConfig() *Config {

	brokers := os.Getenv("KAFKA_BROKERS")

	if brokers == "" {
		brokers = "localhost:9092"
	}

	group := os.Getenv("KAFKA_GROUP_ID")

	if group == "" {
		group = "notification-service"
	}

	return &Config{

		Brokers: strings.Split(
			brokers,
			",",
		),

		GroupID: group,
	}
}
