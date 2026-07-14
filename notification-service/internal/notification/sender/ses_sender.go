package sender

import "context"

type SESSender struct {
	// AWS client will go here later
}

func NewSESSender() *SESSender {
	return &SESSender{}
}

func (s *SESSender) Send(
	ctx context.Context,
	recipient string,
	subject string,
	body string,
) error {

	// TODO:
	// Integrate AWS SES

	return nil
}
