module com.ecommerce/search-service

go 1.27.0

require (
	com.ecommerce/shared v0.0.0
	github.com/opensearch-project/opensearch-go/v4 v4.7.3
	github.com/go-chi/chi/v5 v5.3.2
	github.com/go-chi/cors v1.2.2
	github.com/segmentio/kafka-go v0.4.47
	github.com/stretchr/testify v1.11.1
)

require (
	github.com/davecgh/go-spew v1.1.1 // indirect
	github.com/go-logr/logr v1.4.2 // indirect
	github.com/go-logr/stdr v1.2.2 // indirect
	github.com/golang-jwt/jwt/v5 v5.3.1 // indirect
	github.com/klauspost/compress v1.18.0 // indirect
	github.com/pierrec/lz4/v4 v4.1.15 // indirect
	github.com/pmezard/go-difflib v1.0.0 // indirect
	github.com/rogpeppe/go-internal v1.16.0 // indirect
	go.opentelemetry.io/auto/sdk v1.1.0 // indirect
	go.opentelemetry.io/otel v1.35.0 // indirect
	go.opentelemetry.io/otel/metric v1.35.0 // indirect
	go.opentelemetry.io/otel/sdk v1.32.0 // indirect
	go.opentelemetry.io/otel/trace v1.35.0 // indirect
	golang.org/x/net v0.38.0 // indirect
	golang.org/x/sys v0.32.0 // indirect
	gopkg.in/yaml.v3 v3.0.1 // indirect
)

replace com.ecommerce/shared => ../shared
