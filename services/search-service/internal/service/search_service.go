package service

import (
	"bytes"
	"context"
	"encoding/json"
	"fmt"
	"log"
	"math"
	"strings"

	"com.ecommerce/search-service/internal/model"
	"github.com/opensearch-project/opensearch-go/v4/opensearchapi"
)

type SearchService struct {
	os *opensearchapi.Client
}

func NewSearchService(os *opensearchapi.Client) *SearchService {
	return &SearchService{os: os}
}

func (s *SearchService) FindProductAdvance(
	ctx context.Context,
	keyword string,
	page, size int,
	brand, category, attribute string,
	minPrice, maxPrice *float64,
	sortType string,
) (*model.ProductListGetVm, error) {
	if s.os == nil {
		return &model.ProductListGetVm{
			Products:      []model.ProductGetVm{},
			PageNo:        page,
			PageSize:      size,
			TotalElements: 0,
			TotalPages:    0,
			IsLast:        true,
			Aggregations:  map[string]map[string]int{},
		}, nil
	}

	mustQueries := []map[string]any{}
	shouldQueries := []map[string]any{}

	if strings.TrimSpace(keyword) != "" {
		shouldQueries = append(shouldQueries, map[string]any{
			"multi_match": map[string]any{
				"query":     keyword,
				"fields":    []string{"name^3", "brand^2", "categories"},
				"fuzziness": "AUTO",
			},
		})
	}

	if brand != "" {
		mustQueries = append(mustQueries, map[string]any{
			"term": map[string]any{"brand.keyword": brand},
		})
	}
	if category != "" {
		mustQueries = append(mustQueries, map[string]any{
			"term": map[string]any{"categories": category},
		})
	}
	if attribute != "" {
		mustQueries = append(mustQueries, map[string]any{
			"term": map[string]any{"attributes": attribute},
		})
	}

	if minPrice != nil || maxPrice != nil {
		rangeQuery := map[string]any{}
		if minPrice != nil {
			rangeQuery["gte"] = *minPrice
		}
		if maxPrice != nil {
			rangeQuery["lte"] = *maxPrice
		}
		mustQueries = append(mustQueries, map[string]any{
			"range": map[string]any{"price": rangeQuery},
		})
	}

	boolQuery := map[string]any{}
	if len(mustQueries) > 0 {
		boolQuery["must"] = mustQueries
	}
	if len(shouldQueries) > 0 {
		boolQuery["should"] = shouldQueries
	}

	from := page * size
	queryBody := map[string]any{
		"from": from,
		"size": size,
	}

	if len(boolQuery) > 0 {
		queryBody["query"] = map[string]any{"bool": boolQuery}
	} else {
		queryBody["query"] = map[string]any{"match_all": map[string]any{}}
	}

	var buf bytes.Buffer
	if err := json.NewEncoder(&buf).Encode(queryBody); err != nil {
		return nil, err
	}

	searchReq := opensearchapi.SearchReq{
		Indices: []string{"product"},
		Body:    &buf,
		Params: opensearchapi.SearchParams{
			TrackTotalHits: json.RawMessage(`true`),
		},
	}

	searchResp, err := s.os.Search(ctx, &searchReq)
	if err != nil {
		log.Printf("[Search Service] Warning: OpenSearch Search failed: %v", err)
		return &model.ProductListGetVm{
			Products:      []model.ProductGetVm{},
			PageNo:        page,
			PageSize:      size,
			TotalElements: 0,
			TotalPages:    0,
			IsLast:        true,
			Aggregations:  map[string]map[string]int{},
		}, nil
	}

	totalHits := int64(searchResp.Hits.Total.Value)

	products := []model.ProductGetVm{}
	for _, hit := range searchResp.Hits.Hits {
		var src map[string]any
		if err := json.Unmarshal(hit.Source, &src); err != nil {
			continue
		}

		idVal, _ := src["id"].(float64)
		nameVal, _ := src["name"].(string)
		slugVal, _ := src["slug"].(string)
		priceVal, _ := src["price"].(float64)
		allowedVal, _ := src["isAllowedToOrder"].(bool)
		pubVal, _ := src["isPublished"].(bool)
		featVal, _ := src["isFeatured"].(bool)
		visVal, _ := src["isVisibleIndividually"].(bool)

		var thumbID *int64
		if t, ok := src["thumbnailMediaId"].(float64); ok {
			i := int64(t)
			thumbID = &i
		}

		products = append(products, model.ProductGetVm{
			ID:                    int64(idVal),
			Name:                  nameVal,
			Slug:                  slugVal,
			ThumbnailMediaID:      thumbID,
			Price:                 priceVal,
			IsAllowedToOrder:      allowedVal,
			IsPublished:           pubVal,
			IsFeatured:            featVal,
			IsVisibleIndividually: visVal,
		})
	}

	totalPages := 0
	if size > 0 {
		totalPages = int(math.Ceil(float64(totalHits) / float64(size)))
	}
	isLast := (page + 1) >= totalPages

	return &model.ProductListGetVm{
		Products:      products,
		PageNo:        page,
		PageSize:      size,
		TotalElements: totalHits,
		TotalPages:    totalPages,
		IsLast:        isLast,
		Aggregations:  map[string]map[string]int{},
	}, nil
}

func (s *SearchService) AutoComplete(ctx context.Context, keyword string) (*model.ProductNameListVm, error) {
	if s.os == nil || strings.TrimSpace(keyword) == "" {
		return &model.ProductNameListVm{ProductNames: []model.ProductNameGetVm{}}, nil
	}

	queryBody := map[string]any{
		"size": 10,
		"query": map[string]any{
			"match_phrase_prefix": map[string]any{
				"name": keyword,
			},
		},
		"_source": []string{"id", "name"},
	}

	var buf bytes.Buffer
	if err := json.NewEncoder(&buf).Encode(queryBody); err != nil {
		return nil, err
	}

	searchReq := opensearchapi.SearchReq{
		Indices: []string{"product"},
		Body:    &buf,
	}

	searchResp, err := s.os.Search(ctx, &searchReq)
	if err != nil {
		return &model.ProductNameListVm{ProductNames: []model.ProductNameGetVm{}}, nil
	}

	list := []model.ProductNameGetVm{}
	for _, hit := range searchResp.Hits.Hits {
		var src map[string]any
		if err := json.Unmarshal(hit.Source, &src); err != nil {
			continue
		}
		idVal, _ := src["id"].(float64)
		nameVal, _ := src["name"].(string)
		list = append(list, model.ProductNameGetVm{
			ID:   int64(idVal),
			Name: nameVal,
		})
	}

	return &model.ProductNameListVm{ProductNames: list}, nil
}

func (s *SearchService) IndexProduct(ctx context.Context, p model.ProductDoc) error {
	if s.os == nil {
		return nil
	}
	data, err := json.Marshal(p)
	if err != nil {
		return err
	}

	docID := fmt.Sprintf("%d", p.ID)
	indexReq := opensearchapi.IndexReq{
		Index:      "product",
		DocumentID: docID,
		Body:       bytes.NewReader(data),
	}

	_, err = s.os.Index(ctx, indexReq)
	return err
}

func (s *SearchService) DeleteProduct(ctx context.Context, id int64) error {
	if s.os == nil {
		return nil
	}

	docID := fmt.Sprintf("%d", id)
	deleteReq := opensearchapi.DocumentDeleteReq{
		Index:      "product",
		DocumentID: docID,
	}

	_, err := s.os.Document.Delete(ctx, deleteReq)
	return err
}
