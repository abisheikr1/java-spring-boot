package com.billing_software.billing_software.dbImplementation;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.billing_software.billing_software.dbInterface.DashboardInterface;
import com.billing_software.billing_software.models.dashboard.DashboardData;
import com.billing_software.billing_software.models.customer.Customer;
import com.billing_software.billing_software.models.financialDocument.FinancialDocument;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardImpl implements DashboardInterface {

    private final ReactiveMongoTemplate mongoTemplate;

    public DashboardImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public DashboardData getDashBoardData(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        DashboardData dashboardData = new DashboardData();

        // Fetch monthly sales trend
        List<DashboardData.MonthlySalesTrend> monthlySalesTrend = getMonthlySalesData(start, end);

        // Fetch top selling products
        List<DashboardData.TopSellingProduct> topSellingProducts = getTopSellingProducts(start, end);

        // Fetch top buying customers
        List<DashboardData.TopBuyingCustomer> topBuyingCustomers = getTopBuyingCustomers(start, end);

        // Fetch total sales and transactions
        List<FinancialDocument> financialDocuments = mongoTemplate.find(
                Query.query(Criteria.where("documentDate").gte(start).lte(end)),
                FinancialDocument.class).collectList().block(); // Blocking here to get the result

        double totalSales = financialDocuments.stream()
                .mapToDouble(FinancialDocument::getTotalAmount)
                .sum();
        int totalTransactions = financialDocuments.stream()
                .mapToInt(doc -> doc.getItems().size())
                .sum();

        dashboardData.setMonthlySalesTrend(monthlySalesTrend);
        dashboardData.setTopSellingProducts(topSellingProducts);
        dashboardData.setTopBuyingCustomers(topBuyingCustomers);
        dashboardData.setTotalSales(totalSales);
        dashboardData.setTotalTransactions(totalTransactions);

        // Fetch new customers
        long newCustomersCount = mongoTemplate.count(
                Query.query(Criteria.where("createdDate").gte(start).lte(end)),
                Customer.class).block(); // Blocking to get the new customers count

        dashboardData.setNewCustomers((int) newCustomersCount);

        // Calculate new customer rate
        long totalCustomersCount = mongoTemplate.count(new Query(), Customer.class).block(); // Blocking here
        dashboardData.calculateNewCustomerRate((int) totalCustomersCount, (int) newCustomersCount);

        // Calculate average order value
        dashboardData.calculateAverageOrderValue();

        return dashboardData; // Return the DashboardData object
    }

    public List<DashboardData.MonthlySalesTrend> getMonthlySalesData(LocalDate start, LocalDate end) {
        List<DashboardData.MonthlySalesTrend> monthlySalesTrend = new ArrayList<>();

        LocalDate current = start.withDayOfMonth(1); // Set to the first day of the start month
        while (current.isBefore(end) || current.isEqual(end)) {
            // Get the start and end dates for the current month
            LocalDate monthStart = current.withDayOfMonth(1);
            LocalDate monthEnd = current.withDayOfMonth(current.lengthOfMonth()); // Last day of the month

            // Call your existing method for the current month's data
            List<DashboardData.MonthlySalesTrend> monthlyData = getMonthlySalesTrend(monthStart, monthEnd);

            // Assuming getMonthlySalesTrend returns a list and you need one record for the
            // month
            if (!monthlyData.isEmpty()) {
                monthlyData.get(0).setMonth(current.getMonth().name());
                monthlyData.get(0).setYear(current.getYear());
                monthlySalesTrend.add(monthlyData.get(0)); // Add only the first record for the month
            }

            // Move to the next month
            current = current.plusMonths(1);
        }

        return monthlySalesTrend;
    }

    // Complex Aggregation Methods
    private List<DashboardData.MonthlySalesTrend> getMonthlySalesTrend(LocalDate start, LocalDate end) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("documentDate").gte(start).lte(end)),
                Aggregation.group("documentDate")
                        .sum("totalAmount").as("totalSales")
                        .count().as("totalTransactions"),
                Aggregation.project("totalSales", "totalTransactions")
                        .and("documentDate").dateAsFormattedString("%Y-%m").as("month"));

        return mongoTemplate.aggregate(aggregation, FinancialDocument.class, DashboardData.MonthlySalesTrend.class)
                .collectList().blockOptional().orElse(null);
    }

    private List<DashboardData.TopSellingProduct> getTopSellingProducts(LocalDate start, LocalDate end) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("documentDate").gte(start).lte(end)),
                Aggregation.unwind("items"),
                Aggregation.group("items.itemId", "items.name")
                        .sum("items.quantity").as("quantitySold")
                        .sum("items.totalAmount").as("totalRevenue"),
                Aggregation.project("quantitySold", "totalRevenue")
                        .and("_id.itemId").as("productId")
                        .and("_id.name").as("productName"),
                Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "quantitySold"),
                Aggregation.limit(10));

        return mongoTemplate.aggregate(aggregation, FinancialDocument.class, DashboardData.TopSellingProduct.class)
                .collectList().blockOptional().orElse(null);
    }

    private List<DashboardData.TopBuyingCustomer> getTopBuyingCustomers(LocalDate start, LocalDate end) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("documentDate").gte(start).lte(end)),
                Aggregation.group("customerId", "customerName")
                        .sum("totalAmount").as("totalSpent")
                        .count().as("totalTransactions"),
                Aggregation.project("totalSpent", "totalTransactions")
                        .and("_id.customerId").as("customerId")
                        .and("_id.customerName").as("customerName"),
                Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "totalSpent"),
                Aggregation.limit(10));

        return mongoTemplate.aggregate(aggregation, FinancialDocument.class, DashboardData.TopBuyingCustomer.class)
                .collectList().blockOptional().orElse(null);
    }
}
