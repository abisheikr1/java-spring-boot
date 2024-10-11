package com.billing_software.billing_software.repositories;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.billing_software.billing_software.models.financialDocument.FinancialDocument;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FinancialDocumentRepository extends ReactiveMongoRepository<FinancialDocument, String> {

    @Query("{invoiceId:?0}")
    Mono<FinancialDocument> findAllByInvoiceId(String invoiceId);

    @Query("{invoiceId:?0}")
    Mono<FinancialDocument> findByDocNumber(String invoiceId);

    @Query("{ 'documentDate': { '$gte': ?0, '$lte': ?1 } }")
    Flux<FinancialDocument> findAllByDocumentDateRange(LocalDateTime startDate, LocalDateTime endDate);

}