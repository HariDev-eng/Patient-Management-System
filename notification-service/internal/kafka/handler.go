package kafka

import "context"

type Handler interface {
	Handle(
		ctx context.Context,
		data []byte,
	) error
}
