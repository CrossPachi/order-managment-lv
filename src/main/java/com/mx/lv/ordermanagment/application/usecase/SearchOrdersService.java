package com.mx.lv.ordermanagment.application.usecase;

import com.mx.lv.ordermanagment.application.port.in.SearchOrdersUseCase;
import com.mx.lv.ordermanagment.application.port.out.LoadOrdersPort;
import com.mx.lv.ordermanagment.domain.model.Order;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

public class SearchOrdersService implements SearchOrdersUseCase {

    private static final int MAX_ALLOWED_ERRORS = 1;

    private final LoadOrdersPort loadOrdersPort;
    private final LevenshteinDistance levenshteinDistance;

    public SearchOrdersService(LoadOrdersPort loadOrdersPort) {
        this.loadOrdersPort = loadOrdersPort;
        this.levenshteinDistance =
                LevenshteinDistance.getDefaultInstance();
    }

    @Override
    public List<Order> searchOrders(String findText) {
        String searchText = normalizeText(findText);

        if (searchText.isBlank()) {
            return List.of();
        }

        return loadOrdersPort.loadOrders().stream()
                .filter(order -> matchesOrder(order, searchText))
                .toList();
    }

    private boolean matchesOrder(Order order, String searchText) {
        return containsText(order.orderRef(), searchText)
                || containsText(order.orderStatus(), searchText)
                || matchesFlexibleText(order.storeName(), searchText)
                || order.items().stream()
                .anyMatch(item ->
                        matchesFlexibleText(
                                item.displayName(),
                                searchText
                        )
                );
    }

    private boolean containsText(String field, String searchText) {
        return normalizeText(field).contains(searchText);
    }

    private boolean matchesFlexibleText(
            String field,
            String searchText
    ) {
        String normalizedField = normalizeText(field);

        if (normalizedField.contains(searchText)) {
            return true;
        }

        String[] fieldWords = normalizedField.split(" ");
        String[] searchWords = searchText.split(" ");

        for (String searchWord : searchWords) {
            boolean wordMatches = false;

            for (String fieldWord : fieldWords) {
                if (fieldWord.contains(searchWord)
                        || hasMinimumSpellingError(
                        fieldWord,
                        searchWord
                )) {
                    wordMatches = true;
                    break;
                }
            }

            if (!wordMatches) {
                return false;
            }
        }

        return true;
    }

    private boolean hasMinimumSpellingError(
            String fieldWord,
            String searchWord
    ) {
        int distance = levenshteinDistance.apply(
                fieldWord,
                searchWord
        );

        return distance <= MAX_ALLOWED_ERRORS;
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }

        String normalizedText = text
                .toLowerCase(Locale.ROOT);

        return Normalizer.normalize(
                        normalizedText,
                        Normalizer.Form.NFD
                )
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}