package com.petrovoleh.model;

import java.util.List;

public class OrderListResponse {
    private List<Order> list;
    private int totalPages;

    public OrderListResponse(List<Order> list, int totalPages) {
        this.list = list;
        this.totalPages = totalPages;
    }

    public List<Order> getList() {
        return list;
    }

    public void setList(List<Order> list) {
        this.list = list;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
