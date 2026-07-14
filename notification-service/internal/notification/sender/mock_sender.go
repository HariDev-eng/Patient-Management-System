package sender

import (
	"context"
	"log"
)

type MockSender struct{}

func NewMockSender() *MockSender {
	return &MockSender{}
}

func (s *MockSender) Send(
	ctx context.Context,
	req EmailRequest,
) error {

	log.Println("========== MOCK EMAIL ==========")
	log.Println("To:", req.Recipient)
	log.Println("Subject:", req.Subject)
	log.Println("Body:")
	log.Println(req.Body)
	log.Println("===============================")

	return nil
}
