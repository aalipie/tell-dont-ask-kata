package it.gabrieletondi.telldontaskkata.domain;

import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.APPROVED;
import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.CREATED;
import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.REJECTED;
import static it.gabrieletondi.telldontaskkata.domain.OrderStatus.SHIPPED;

import it.gabrieletondi.telldontaskkata.useCase.ApprovedOrderCannotBeRejectedException;
import it.gabrieletondi.telldontaskkata.useCase.OrderCannotBeShippedException;
import it.gabrieletondi.telldontaskkata.useCase.OrderCannotBeShippedTwiceException;
import it.gabrieletondi.telldontaskkata.useCase.RejectedOrderCannotBeApprovedException;
import it.gabrieletondi.telldontaskkata.useCase.ShippedOrdersCannotBeChangedException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private BigDecimal total;
    private String currency;
    private List<OrderItem> items;
    private BigDecimal tax;
    private OrderStatus status;
    private int id;

    public Order() {
        this.total = new BigDecimal("0.00");
        this.currency = "EUR";
        this.items = new ArrayList<>();
        this.tax = new BigDecimal("0.00");
        this.status = CREATED;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private void validate(Boolean approvalRequest) {

        if (getStatus().equals(SHIPPED)) {
            throw new ShippedOrdersCannotBeChangedException();
        }

        if (approvalRequest && getStatus().equals(REJECTED)) {
            throw new RejectedOrderCannotBeApprovedException();
        }

        if (!approvalRequest && getStatus().equals(APPROVED)) {
            throw new ApprovedOrderCannotBeRejectedException();
        }
    }

    public void approveOrder(boolean approvalRequest) {
        validate(approvalRequest);
        setStatus(approvalRequest ? APPROVED : REJECTED);
    }

    public void addItem(Product product, int quantity) {
        OrderItem orderItem = new OrderItem(product, quantity);
        items.add(orderItem);
        this.total = total.add(product.getTaxedAmount(quantity));
        this.tax = tax.add(product.getTaxAmount(quantity));
    }

    public void ship() {
        if (status.equals(CREATED) || status.equals(REJECTED)) {
            throw new OrderCannotBeShippedException();
        }

        if (status.equals(SHIPPED)) {
            throw new OrderCannotBeShippedTwiceException();
        }

        this.status = SHIPPED;
    }

}
