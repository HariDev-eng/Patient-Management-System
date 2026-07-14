package sender

import "context"

type EmailRequest struct {
	Recipient string
	Subject   string
	Body      string
}

type Sender interface {
	Send(
		ctx context.Context,
		req EmailRequest,
	) error
}
