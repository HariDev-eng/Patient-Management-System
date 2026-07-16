package messaging

import "context"

type Publisher interface {
	PublishRaw(
		ctx context.Context,
		destination string,
		payload []byte,
	) error
}
