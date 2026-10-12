package handler

import (
	"net/http"
	"strconv"

	"com.ecommerce/search-service/internal/service"
	"com.ecommerce/shared/response"
	"github.com/go-chi/chi/v5"
)

type SearchHandler struct {
	svc *service.SearchService
}

func NewSearchHandler(svc *service.SearchService) *SearchHandler {
	return &SearchHandler{svc: svc}
}

func (h *SearchHandler) RegisterRoutes(r chi.Router) {
	r.Get("/actuator/health", h.HealthCheck)
	r.Get("/search/actuator/health", h.HealthCheck)

	// Context root: /search and direct root
	for _, prefix := range []string{"/search", ""} {
		h.registerEndpoints(r, prefix)
	}
}

func (h *SearchHandler) registerEndpoints(r chi.Router, prefix string) {
	r.Get(prefix+"/storefront/catalog-search", h.CatalogSearch)
	r.Get(prefix+"/storefront/search_suggest", h.SearchSuggest)
}

func (h *SearchHandler) HealthCheck(w http.ResponseWriter, r *http.Request) {
	response.Health(w, "opensearch")
}

func intQueryDefault(value string, fallback int) int {
	if value == "" {
		return fallback
	}
	parsed, err := strconv.Atoi(value)
	if err != nil {
		return fallback
	}
	return parsed
}

func (h *SearchHandler) CatalogSearch(w http.ResponseWriter, r *http.Request) {
	q := r.URL.Query()
	keyword := q.Get("keyword")
	page := intQueryDefault(q.Get("page"), 0)
	size := intQueryDefault(q.Get("size"), 12)
	brand := q.Get("brand")
	category := q.Get("category")
	attribute := q.Get("attribute")
	sortType := q.Get("sortType")
	if sortType == "" {
		sortType = "DEFAULT"
	}

	var minPrice *float64
	if minP := q.Get("minPrice"); minP != "" {
		if val, err := strconv.ParseFloat(minP, 64); err == nil {
			minPrice = &val
		}
	}

	var maxPrice *float64
	if maxP := q.Get("maxPrice"); maxP != "" {
		if val, err := strconv.ParseFloat(maxP, 64); err == nil {
			maxPrice = &val
		}
	}

	res, err := h.svc.FindProductAdvance(r.Context(), keyword, page, size, brand, category, attribute, minPrice, maxPrice, sortType)
	if err != nil {
		response.InternalError(w, r, err.Error())
		return
	}

	response.WriteJSON(w, http.StatusOK, res)
}

func (h *SearchHandler) SearchSuggest(w http.ResponseWriter, r *http.Request) {
	keyword := r.URL.Query().Get("keyword")
	res, err := h.svc.AutoComplete(r.Context(), keyword)
	if err != nil {
		response.InternalError(w, r, err.Error())
		return
	}
	response.WriteJSON(w, http.StatusOK, res)
}
