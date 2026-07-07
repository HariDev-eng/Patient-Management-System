package config

import (
	"log"

	"github.com/spf13/viper"
)

type Config struct {
	Port string

	DBHost     string
	DBPort     string
	DBUser     string
	DBPassword string
	DBName     string

	KafkaBrokers string
	RabbitMQURL  string

	AWSRegion string
}

func LoadConfig() *Config {

	viper.SetConfigFile(".env")

	if err := viper.ReadInConfig(); err != nil {
		log.Fatalf("failed to load config: %v", err)
	}

	return &Config{
		Port: viper.GetString("PORT"),

		DBHost:     viper.GetString("DB_HOST"),
		DBPort:     viper.GetString("DB_PORT"),
		DBUser:     viper.GetString("DB_USER"),
		DBPassword: viper.GetString("DB_PASSWORD"),
		DBName:     viper.GetString("DB_NAME"),

		KafkaBrokers: viper.GetString("KAFKA_BROKERS"),
		RabbitMQURL:  viper.GetString("RABBITMQ_URL"),

		AWSRegion: viper.GetString("AWS_REGION"),
	}
}
